package com.ssginc.showpinglive.dto.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chat") // MongoDB Collection 이름
public class ChatDto {
    @Id
    private String id; // MongoDB의 _id 필드 (ObjectId를 String으로 매핑)

    @Field("chat_no")
    private Long chatNo; // chat_no 필드 (Int64)

    @Field("chat_stream_no")
    private Long chatStreamNo; // 스트림 번호 (Int64)

    @Field("chat_member_no")
    private Long chatMemberNo; // 회원 번호 (Int64)

    @Field("chat_room_no")
    private Long chatRoomNo; // 채팅방 번호 (Int64)

    @Field("chat_message")
    private String chatMessage; // 메시지 내용

    @Field("chat_created_at")
    private LocalDateTime chatCreatedAt; // 생성 시간
}