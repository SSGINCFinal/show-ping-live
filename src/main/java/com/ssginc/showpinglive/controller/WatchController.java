package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.VodResponseDto;
import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("watch")
@RequiredArgsConstructor
public class WatchController {

    private final StreamService streamService;

    @GetMapping("/vod/{streamNo}")
    public String watchVod(@PathVariable Long streamNo, Model model) {
        VodResponseDto vodDto = streamService.getVodByNo(streamNo);
        System.out.println(vodDto.toString());
        model.addAttribute("vodDto", vodDto);
        return "watch/vod";
    }
}
