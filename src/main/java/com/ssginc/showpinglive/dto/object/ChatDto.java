package com.ssginc.showpinglive.dto.object;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("_id")
    private String id; // MongoDB의 _id 필드 (ObjectId를 String으로 매핑)

    @JsonProperty("chat_member_id")
    private String chatMemberId;

    @JsonProperty("chat_room_no")
    private Long chatRoomNo;

    @JsonProperty("chat_message")
    private String chatMessage;

    @JsonProperty("chat_created_at")
//    @JsonFormat(shape = JsonFormat.Shape.STRING,
//            pattern = "yyyy. M. d. a h:mm:ss",
//            locale = "ko",
//            timezone = "Asia/Seoul")
    private String chatCreatedAt; // 생성 시간
}