package com.ssginc.showpinglive.chat;

import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "de.flapdoodle.mongodb.embedded.version=5.0.5")
public class MongoDbConnectionTest {

    @Autowired
    private ChatRepository chatRepository;

    @Test
    public void testMongoDbConnection() {
        // 1. 테스트 데이터 생성
        ChatDto message = new ChatDto();
        message.setChatStreamNo(101L);
        message.setChatMemberNo(1001L);
        message.setChatRoomNo(2001L);
        message.setChatMessage("테스트 메시지");
        message.setChatCreatedAt(LocalDateTime.now());

        // 2. 데이터 저장
        ChatDto savedMessage = chatRepository.save(message);

        // 3. 저장된 데이터 검증
        assertThat(savedMessage).isNotNull(); // 저장된 객체가 null이 아닌지 확인
        assertThat(savedMessage.getId()).isNotNull(); // _id 필드가 생성되었는지 확인
        assertThat(savedMessage.getChatStreamNo()).isEqualTo(101L); // 데이터 값 검증
        assertThat(savedMessage.getChatMemberNo()).isEqualTo(1001L);
        assertThat(savedMessage.getChatRoomNo()).isEqualTo(2001L);
        assertThat(savedMessage.getChatMessage()).isEqualTo("테스트 메시지");

        // 4. 저장된 데이터를 다시 조회하여 검증
        ChatDto retrievedMessage = chatRepository.findById(savedMessage.getId()).orElse(null);
        assertThat(retrievedMessage).isNotNull(); // 조회된 객체가 null이 아닌지 확인
        assertThat(retrievedMessage.getChatStreamNo()).isEqualTo(101L); // 데이터 값 검증
    }
}
