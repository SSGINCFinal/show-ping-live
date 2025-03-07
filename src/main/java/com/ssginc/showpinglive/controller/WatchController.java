package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.request.WatchRequestDto;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.dto.response.WatchResponseDto;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.Watch;
import com.ssginc.showpinglive.jwt.JwtUtil;
import com.ssginc.showpinglive.service.MemberService;
import com.ssginc.showpinglive.service.StreamService;
import com.ssginc.showpinglive.service.WatchService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("watch")
@RequiredArgsConstructor
public class WatchController {

    private final StreamService streamService;

    private final WatchService watchService;

    private final MemberService memberService;

    private final JwtUtil jwtUtil;

    @GetMapping("/vod/{streamNo}")
    public String watchVod(@PathVariable Long streamNo, Model model) {
        StreamResponseDto vodDto = streamService.getVodByNo(streamNo);
        System.out.println(vodDto.toString());
        model.addAttribute("vodDto", vodDto);
        return "watch/vod";
    }

    @GetMapping("/history")
    public String watchHistory(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        String memberId = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("accessToken")) {
                memberId = jwtUtil.getUsernameFromToken(cookie.getValue());
                break;
            }
        }

        Member member = memberService.findMemberById(memberId);
        model.addAttribute("member", member);

        return "watch/history";
    }

    @GetMapping("/history/{memberNo}")
    public ResponseEntity<Map<String, Object>> getWatchHistory(@PathVariable Long memberNo) {
        List<WatchResponseDto> historyList = watchService.getWatchHistoryByMemberNo(memberNo);

        Map<String, Object> result = new HashMap<>();
        result.put("historyList", historyList);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/insert")
    public ResponseEntity<Watch> insertWatchHistory(@RequestBody WatchRequestDto watchRequestDto) {
        System.out.println(watchRequestDto);

        Watch result = watchService.insertWatchHistory(watchRequestDto);

        return ResponseEntity.ok(result);
    }

}
