package com.ssginc.showpinglive.chat;

import com.ssginc.showpinglive.config.WebSocketConfig;
import com.ssginc.showpinglive.repository.ChatRepository;
import com.ssginc.showpinglive.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.lettuce.core.internal.Futures.await;
import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "de.flapdoodle.mongodb.embedded.version=5.0.5")
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = WebSocketConfig.class)
public class ChatMessageIntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @Test
    @DisplayName("TDD-CHT-001: 일반 메시지 전송 시 MongoDB 저장 확인")
    void testSendMessageAndCheckInMongo() throws InterruptedException {
        chatRepository.deleteAll();

        int numUsers = 10;
        int messagesPerUser = 1; // 총 numUsers * messagesPerUser =  메시지
        int totalMessages = numUsers * messagesPerUser;
        ExecutorService executor = Executors.newFixedThreadPool(numUsers);
        CountDownLatch latch = new CountDownLatch(totalMessages);
        AtomicInteger sendFailures = new AtomicInteger(0);

        for (int i = 1; i < numUsers+1; i++) {
            final String userId = "user" + i;
            executor.submit(() -> {
                for (int j = 1; j < messagesPerUser+1; j++) {
                    String chatMessage = "메시지 " + j + " from " + userId;
                    String chatCreatedAt = LocalDateTime.now().toString();
                    String chatRole = "ROLE_USER";
                    try {
                        // DB 저장은 KafkaConsumer를 통해 이루어지므로, 여기서는 ChatService 호출만 수행
                        chatService.saveChatMessage(userId, 1L, chatMessage, chatRole ,chatCreatedAt);
                    } catch (Exception e) {
                        sendFailures.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                    // 10초 내에 전송되도록 딜레이 분산 (0~10초 사이 랜덤 딜레이)
                    try {
                        Thread.sleep(ThreadLocalRandom.current().nextInt(0, 10));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // KafkaConsumer가 비동기로 MongoDB에 저장할 시간을 고려하여 추가 대기 (예: 5초)
        Thread.sleep(5000);

        // MongoDB에 저장된 메시지 개수 검증
        System.out.println("테스트 유저 명 수: "  + numUsers);
        System.out.println("유저당 보내는 메세지: " + messagesPerUser);
        System.out.println("총 전송된 메시지: " + totalMessages);
        System.out.println("전송 실패 건수: " + sendFailures.get());

        await().atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> assertThat(chatRepository.count()).isEqualTo(totalMessages));


        latch.await(3, TimeUnit.SECONDS); // 예: 10초 안에 모든 메시지 전송 완료
        executor.shutdown();

    }
}