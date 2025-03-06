package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송용

    private long kafka_time = 0;
    private long kafka_count = 0;

    /**
     * Kafka에서 ChatDto 메시지를 소비하고 처리
     * @param chatDto Kafka에서 수신한 채팅 메시지 객체
     */
    @KafkaListener(topics = "chat-messages", groupId = "chat-consumer-group")
    public void consumeMessage(ChatDto chatDto) {
        long startTime = System.currentTimeMillis();
        kafka_count++;

        System.out.println("Received chat message: " + chatDto);


        try {
            // 특정 채팅방(/sub/chat/room/{chatRoomNo})으로 메시지를 실시간 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatDto.getChatRoomNo(), chatDto);
            chatRepository.save(chatDto);
            System.out.println(kafka_count + "받은 chat 몽고디비에 저장됨.");

            long endTime = System.currentTimeMillis();
            System.out.println(">>>>>>> 메세지 처리시간 : " + (endTime - startTime) + "ms");
            kafka_time = endTime - startTime;

            System.out.println(kafka_count + ">> kafka_time ==========> " + kafka_time);
            System.out.println("Chat saved to MongoDB " + chatDto.getChatMessage());
        } catch (Exception e) {
            System.err.println("Error processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
