package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.request.WatchRequestDto;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.dto.response.WatchResponseDto;
import com.ssginc.showpinglive.entity.Member;
import com.ssginc.showpinglive.entity.Watch;
import com.ssginc.showpinglive.service.MemberService;
import com.ssginc.showpinglive.service.StreamService;
import com.ssginc.showpinglive.service.WatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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


    @GetMapping("/vod/{streamNo}")
    public String watchVod(@PathVariable Long streamNo, Model model) {
        StreamResponseDto vodDto = streamService.getVodByNo(streamNo);
        System.out.println(vodDto.toString());
        model.addAttribute("vodDto", vodDto);
        return "watch/vod";
    }

    /**
     * 로그인한 사용자 시청내역 페이지 메소드
     * @param userDetails 로그인한 사용자 객체
     * @param model 타임리프에 전달할 Model 객체
     * @return 라이브 메인 페이지 (타임리프)
     */
    @GetMapping("/history")
    public String watchHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 현재 로그인한 사용자 불러오기
        if (userDetails != null) {
            Member member = memberService.findMemberById(userDetails.getUsername());
            model.addAttribute("member", member);
        }
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
        Watch result = watchService.insertWatchHistory(watchRequestDto);
        return ResponseEntity.ok(result);
    }

}
