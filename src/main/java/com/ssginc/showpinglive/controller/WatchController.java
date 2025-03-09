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

    /**
     * VOD 페이지 이동을 위한 컨트롤러 메소드
     * @param userDetails 로그인한 사용자 객체
     * @param streamNo 시청할 영상 번호
     * @param model 타임리프에 전달할 Model 객체
     * @return 라이브 메인 페이지 (타임리프)
     */
    @GetMapping("/vod/{streamNo}")
    public String watchVod(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable Long streamNo,
                           Model model) {
        // 로그인 여부 확인
        if (userDetails != null) {
            Member member = memberService.findMemberById(userDetails.getUsername());
            model.addAttribute("member", member);
        }
        else {
            model.addAttribute("member", new Member());
        }

        // VOD 객체 정보 불러오기
        StreamResponseDto vodDto = streamService.getVodByNo(streamNo);
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
    public ResponseEntity<?> getWatchHistory(@PathVariable Long memberNo) {
        List<WatchResponseDto> historyList = watchService.getWatchHistoryByMemberNo(memberNo);

        Map<String, Object> result = new HashMap<>();
        result.put("historyList", historyList);
        return ResponseEntity.ok(result);
    }

    /**
     * 시청 내역 등록 컨트롤러 메소드
     * @param watchRequestDto 시청내역 등록을 위한 요청 DTO (Body를 통해 전달)
     * @return 응답 결과
     */
    @PostMapping("/insert")
    public ResponseEntity<?> insertWatchHistory(@RequestBody WatchRequestDto watchRequestDto) {
        Watch watch = watchService.insertWatchHistory(watchRequestDto);
        return ResponseEntity.ok(watch);
    }

}
