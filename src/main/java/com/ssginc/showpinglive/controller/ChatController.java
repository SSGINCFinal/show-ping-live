package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.jwt.JwtUtil;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.repository.StreamRepository;
import com.ssginc.showpinglive.service.ChatService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * @author juil1-kim
 * 채팅 관련 요청-응답 수행하는 Controller 클래스
 * <p>
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("chat") // REST API의 기본 경로 설정
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송
    private final ChatService chatService; // 채팅 서비스 로직
    private final MemberRepository memberRepository;
    private final StreamRepository streamRepository;
    private final JwtUtil jwtUtil;

    /**
     * 채팅 메시지를 저장하는 공통 로직 처리 메소드
     *
     * @param chatDto 채팅 메시지 객체 (전송자, 채팅방 번호, 메시지 내용, 생성시간)
     * @return 저장된 채팅 메시지 객체
     */
    private ChatDto processAndSaveMessage(ChatDto chatDto) {
        return chatService.saveChatMessage(
                chatDto.getChatMemberId(),
                chatDto.getChatRoomNo(),
                chatDto.getChatMessage(),
                chatDto.getChatCreatedAt()
        );
    }

    /**
     * WebSocket을 통해 클라이언트에서 "/chat/message"로 전송된 메시지를 처리하는 메소드
     * <p>
     * 메시지를 처리하고 저장한 후, 해당 채팅방에 메시지를 브로드캐스트.
     * 금칙어가 포함된 경우, 해당 유저에게 에러 메시지를 전송.
     *
     * @param message   클라이언트에서 전송한 채팅 메시지 객체
     * @param principal 현재 WebSocket 연결 사용자 정보
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto message, Principal principal) {
        System.out.println("[DEBUG] Received message: " + message);
        try {
            ChatDto savedMessage = processAndSaveMessage(message);
            System.out.println("[DEBUG] Saved message: " + savedMessage);
            if (savedMessage.getChatRoomNo() == null) {
                System.err.println("[ERROR] savedMessage.getChatRoomNo() is null!");
            }
            String destination = "/sub/chat/room/" + savedMessage.getChatRoomNo();
            messagingTemplate.convertAndSend(destination, savedMessage);
            System.out.println("[DEBUG] Message sent to destination: " + destination);
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] IllegalArgumentException in sendMessage: " + e.getMessage());
            ChatDto errorResponse = new ChatDto();
            errorResponse.setChatMessage("금칙어가 포함된 메시지는 전송할 수 없습니다.");
            if (principal != null) {
                messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", errorResponse);
                System.out.println("[DEBUG] Principal sent to user: " + principal.getName());
                System.out.println("[DEBUG] Principal: " + principal);
            } else {
                System.err.println("Principal is null; cannot send user-specific error message.");
            }
//            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomNo(), errorResponse);
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in sendMessage: " + e);
            e.printStackTrace();
        }
    }


    /**
     * 채팅방 화면을 렌더링하기 위한 메소드
     * <p>
     * 요청 쿠키에서 accessToken을 추출하여 로그인한 사용자의 정보를 확인한 후, -> 추후 수정 예정(쿠키 사용X)
     * 채팅방 번호와 사용자 아이디를 모델에 추가하여 뷰에 전달.
     *
     * @param request    HttpServletRequest 객체 (쿠키 접근)
     * @param model      뷰에 데이터를 전달하기 위한 Model 객체
     * @param chatRoomNo 채팅방 번호
     * @return 채팅방 뷰 이름 (로그인하지 않은 경우 로그인 페이지로 리다이렉트)
     */
    @GetMapping("chatRoom")
    public String getChatRoom(HttpServletRequest request, Model model, @RequestParam Long chatRoomNo) {
        String memberId = null;
        String memberRole = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
//                    memberId = jwtUtil.getUsernameFromToken(cookie.getValue());
                    memberId = jwtUtil.getUsernameFromToken(cookie.getValue());
//                    memberRole = jwtUtil.getRoleFromToken(cookie.getValue());
                    System.out.println("[DEBUG] memberId: " + memberId);
//                    System.out.println("[DEBUG] memberRole: " + memberRole);
                    break;
                }
            }
        }
        if (memberId == null) {
            // 인증 정보가 없는 경우 로그인 페이지로 리다이렉트
            return "redirect:/login";
        }
        model.addAttribute("chatRoomNo", chatRoomNo);
        model.addAttribute("memberId", memberId);
        return "chat/chatRoom";
    }
}