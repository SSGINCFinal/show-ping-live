package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.ChatRoomResponseDto;
import com.ssginc.showpinglive.entity.ChatRoom;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.service.ChatRoomService;
import com.ssginc.showpinglive.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("webrtc")
@RequiredArgsConstructor
public class WebRtcController {

    private final MemberService memberService;
    private final ChatRoomService chatRoomService;

    @GetMapping("webrtc")
    public String webrtc(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        System.out.println(userDetails);

        if (userDetails != null) {
            Member member = memberService.findMemberById(userDetails.getUsername());
            model.addAttribute("member", member);
        }
        else {
            model.addAttribute("member", new Member());
        }

        return "webrtc/webrtc";
    }

    @GetMapping("watch/{streamNo}")
    public String watch(@AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long streamNo,
                        Model model) {
        System.out.println(userDetails);

        if (userDetails != null) {
            Member member = memberService.findMemberById(userDetails.getUsername());
            model.addAttribute("member", member);
        }
        else {
            model.addAttribute("member", new Member());
        }

        ChatRoomResponseDto chatRoom = chatRoomService.findChatRoomByStreamNo(streamNo);
        System.out.println(chatRoom.getChatRoomNo());
        model.addAttribute("chatRoom", chatRoom);

        return "webrtc/watch";
    }
}
