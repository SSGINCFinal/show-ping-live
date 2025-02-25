package com.ssginc.showpinglive.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("rtmp")
public class RtmpController {

    @GetMapping("/rtmp")
    public String rtmp() {
        return "/rtmp/rtmp";
    }

    @GetMapping("/view")
    public String view() {
        return "/rtmp/rtmpView";
    }

}
