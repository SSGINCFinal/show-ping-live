package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.config.JwtTokenProvider;
import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.repository.MemberRepository;
import com.ssginc.showpinglive.repository.StreamRepository;
import com.ssginc.showpinglive.service.ChatService;
import com.ssginc.showpinglive.service.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("chat") // REST API의 기본 경로 설정
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate; // WebSocket 메시지 전송
    private final ChatService chatService; // 채팅 서비스 로직
    private final MemberRepository memberRepository;
    private final StreamRepository streamRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 공통 메세지 저장 로직
    private ChatDto processAndSaveMessage(ChatDto chatDto) {
        return chatService.saveChatMessage(
                chatDto.getChatMemberId(),
                chatDto.getChatRoomNo(),
                chatDto.getChatMessage(),
                chatDto.getChatCreatedAt()
        );
    }

    // WebSocket 메시지 처리 (클라이언트에서 "/chat/message"로 메시지를 보낼 때 호출)
    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto message) {
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
            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getChatRoomNo(), errorResponse);
        } catch (Exception e) {
            System.err.println("[ERROR] Exception in sendMessage: " + e);
            e.printStackTrace();
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
    // 특정 채팅방의 모든 메시지 조회
    @GetMapping("/room/{chatRoomNo}")
    public List<ChatDto> getMessagesByRoom(@PathVariable Long chatRoomNo) {
        return chatService.findMessagesByRoom(chatRoomNo);
    }

//    @GetMapping("chatRoom")
//    public String getChatRoom(Model model, @RequestParam Long chatRoomNo, Principal principal) {
//        model.addAttribute("chatRoomNo", chatRoomNo);
//        model.addAttribute("memberId", principal.getName());
//        return "chat/chatRoom";
//    }

    @GetMapping("chatRoom")
    public String getChatRoom(HttpServletRequest request, Model model, @RequestParam Long chatRoomNo) {
        String memberId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    memberId = jwtTokenProvider.getMemberId(cookie.getValue());
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


//    // 이후 수정
//    @GetMapping("chatRoom")
//    public String chatRoom(
//            @RequestParam(required = false, defaultValue = "1") Long chatRoomNo,
//            @RequestParam(required = false, defaultValue = "1") Long streamNo,
//            Model model,
//            @AuthenticationPrincipal User user
//    ) {
//
////        // 인증 정보가 없으면 로그인 페이지로 리다이렉트
////        if (user == null || "anonymousUser".equals(user.getUsername())) {
////            return "redirect:/login";
////        }
//
//        Member member = memberRepository.findByMemberId(user.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("해당 memberId의 멤버가 존재하지 않습니다."));
//
//        System.out.println("=======================================");
//        System.out.println("userName: " + user.getUsername());
//        System.out.println("streamNo: " + streamNo);
//        System.out.println("=======================================");
//
//
//
//        // 2) Model에 담기
//        model.addAttribute("chatRoomNo", chatRoomNo);
//        model.addAttribute("streamNo", streamNo);
//        model.addAttribute("memberNo", member.getMemberNo());
//
//        return "chat/chatRoom";  // chatRoom.html
//    }

    @PostMapping("/sendWithToken")
    @ResponseBody
    public ChatDto sendChatMessageWithToken(
            @RequestBody ChatDto chatDto,
            HttpServletRequest request) {

        // 쿠키에서 accessToken 추출
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            throw new RuntimeException("Access token not found in cookies");
        }

        // JWT 토큰에서 MemberId 추출
        String memberId = jwtTokenProvider.getMemberId(token);

        // JPQL을 사용하여 MemberRepository에서 로그인한 유저의 아이디로 Member 엔티티 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found for memberId: " + memberId));

        // 조회된 회원의 memberNo를 ChatDto에 설정 (JSON 형식으로 몽고디비에 전송)
        chatDto.setChatMemberId(memberId);

        // 채팅 메시지를 저장 및 후속 처리
        ChatDto savedMessage = chatService.saveChatMessage(
                member.getMemberId(),
                chatDto.getChatRoomNo(),
                chatDto.getChatMessage(),
                chatDto.getChatCreatedAt()
        );

        // WebSocket을 통해 클라이언트에 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + savedMessage.getChatRoomNo(), savedMessage);

        return savedMessage;
    }

}