package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("chat") // REST API의 기본 경로 설정
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송
    private final ChatService chatService; // 채팅 서비스 로직

    // 공통된 로직 추출
    private ChatDto processAndSaveMessage(ChatDto chatDto) {
        return chatService.saveChatMessage(
                chatDto.getChatStreamNo(),
                chatDto.getChatMemberNo(),
                chatDto.getChatRoomNo(),
                chatDto.getChatMessage(),
                chatDto.getChatCreatedAt()
        );
    }

    // WebSocket 메시지 처리 (클라이언트에서 "/chat/message"로 메시지를 보낼 때 호출)
    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto message) {
        try {
            // 채팅 메시지 저장 및 금칙어 필터링 처리
            ChatDto savedMessage = processAndSaveMessage(message);

            // WebSocket을 통해 클라이언트로 메시지 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getChatRoomNo(), savedMessage);

        } catch (IllegalArgumentException e) {
            // 금칙어 포함 시 예외 처리 (필요 시 클라이언트로 알림)
            ChatDto errorResponse = new ChatDto();
            errorResponse.setChatMessage("금칙어가 포함된 메시지는 전송할 수 없습니다.");
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomNo(), errorResponse);
        }
    }

    // REST API를 통해 채팅 메시지 저장 (HTTP POST 요청 처리)
    @PostMapping("/save")
    public ChatDto saveChat(@RequestBody ChatDto chatDto) {
        try {
            // 채팅 메시지 저장 및 반환
            return processAndSaveMessage(chatDto);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("금칙어가 포함된 메시지는 저장할 수 없습니다.");
        }
    }
    /**
     * 특정 채팅방의 모든 메시지를 가져오는 REST API
     */
    @GetMapping("/room/{chatRoomNo}")
    public List<ChatDto> getMessagesByRoom(@PathVariable Long chatRoomNo) {
        return chatService.findMessagesByRoom(chatRoomNo);
    }

//    @GetMapping("/chatRoom")
//    public String getChatRoom(@RequestParam("chatRoomNo") Long chatRoomNo, Model model) {
//        // 클라이언트에서 전달받은 chatRoomNo를 모델에 추가
//        model.addAttribute("chatRoomNo", chatRoomNo);
//        return "chat/chatRoom"; // chatRoom.html 파일 반환
//    }

    @GetMapping("chatRoom")
    public String getChatRoom() {
        return "chat/chatRoom";
    }
}