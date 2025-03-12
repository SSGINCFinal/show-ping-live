package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.ChatRoomResponseDto;
import com.ssginc.showpinglive.entity.ChatRoom;
import com.ssginc.showpinglive.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("chatRoom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;


    /**
     * 채팅방 생성 API
     * @param data 요청 데이터
     * @return 생성된 ChatRoom 객체 (JSON)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(@RequestBody Map<String, Long> data) {
        System.out.println(data);
        Long streamNo = data.get("streamNo");
        try {
            System.out.println();
            ChatRoom newRoom = chatRoomService.createChatRoom(streamNo);
            System.out.println(newRoom);
            if (newRoom != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(ChatRoomResponseDto.builder()
                        .chatRoomNo(newRoom.getChatRoomNo())
                        .streamNo(streamNo).build());
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("채팅방 생성 중 오류 발생: " + e.getMessage());
        }
    }
}