package com.ssginc.showpinglive.chat;

import com.ssginc.showpinglive.config.WebSocketConfig;
import com.ssginc.showpinglive.dto.response.ChatRoomResponseDto;
import com.ssginc.showpinglive.entity.ChatRoom;
import com.ssginc.showpinglive.entity.Stream;
import com.ssginc.showpinglive.repository.ChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:mysql://localhost:3306/showping?serverTimezone=UTC&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true")
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration(exclude = {WebSocketConfig.class, org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean.class})
public class CreateChatRoomTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("채팅방 생성 테스트")
    void testFindChatRoomByStreamNo() {
        // 1. 기존 채팅방 데이터 삭제 (테스트 환경 정리)
        chatRoomRepository.deleteAll();
        System.out.println("[DEBUG] 1. 삭제 후 채팅방 개수 >> " + chatRoomRepository.count());

        // 2. Dummy Stream 생성 (Stream 엔티티는 최소한의 정보만 설정)
        Stream stream = new Stream();
        stream.setStreamNo(1L);
        System.out.println("[DEBUG] 2. Dummy StreamNo >> " + stream.getStreamNo());
        // 필요에 따라 Stream 엔티티의 다른 필드를 설정

        // 3. ChatRoom 생성 및 저장
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setStream(stream);
        chatRoom.setChatRoomCreatedAt(LocalDateTime.now());

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        System.out.println("[DEBUG] 3. 채팅방 생성 및 저장 >> " + savedRoom);
        assertThat(savedRoom).isNotNull();
        assertThat(savedRoom.getChatRoomNo()).isNotNull();

        // 4. 쿼리 메서드 호출하여 특정 스트림 번호에 해당하는 채팅방 조회
        ChatRoomResponseDto responseDto = chatRoomRepository.findChatRoomByStreamNo(1L);
        System.out.println("[DEBUG] 4. streamNo에 해당하는 채팅방 조회 = " + responseDto);
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getChatRoomNo()).isEqualTo(savedRoom.getChatRoomNo());
        assertThat(responseDto.getStreamNo()).isEqualTo(1L);
    }
}
