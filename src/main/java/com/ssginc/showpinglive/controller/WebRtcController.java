package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.GetStreamRegisterInfoResponseDto;
import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("webrtc")
@RequiredArgsConstructor
public class WebRtcController {

    private final StreamService streamService;

    @GetMapping("webrtc")
    public String webrtc(@AuthenticationPrincipal UserDetails userDetails, Model model){
        String memberId = null;
        if (userDetails != null) {
            memberId = userDetails.getUsername();   // 로그인한 멤버 아이디
        }

        GetStreamRegisterInfoResponseDto streamInfo = streamService.getStreamRegisterInfo(memberId);

        System.out.println("########################");
        System.out.println(streamInfo);
        System.out.println("########################");

        model.addAttribute("streamInfo", streamInfo);

        return "webrtc/webrtc";
    }

    @GetMapping("watch")
    public String watch(){
        return "webrtc/watch";
    }
}
