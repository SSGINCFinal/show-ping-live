package com.ssginc.showpinglive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("webrtc")
public class WebRtcController {

    @GetMapping("webrtc")
    public String webrtc(){
        return "webrtc/webrtc";
    }
}
