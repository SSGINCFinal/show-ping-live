package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;


/**
 * @author dckat
 * HLS와 관련한 요청-응답을 수행하는 컨트롤러 클래스
 * <p>
 */
@Controller
@RequestMapping("hls")
@RequiredArgsConstructor
public class HlsController {

    private final HlsService hlsService;

    /**
     * 영상 제목으로 HLS 파일을 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @return HLS 파일이 포함된 응답객체 (확장자: m3u8)
     */
    @GetMapping(value = "/vod/{title}.m3u8")
    public Mono<?> getHLS(@PathVariable String title) {
        // 불러온 m3u8 파일을 응답으로 전송
        return hlsService.getHLS(title)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE,
                                "application/vnd.apple.mpegurl")
                        .body(resource));
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @GetMapping(value = "/vod/{title}{segment}.ts")
    public Mono<?> getTsSegment(@PathVariable String title,
                                @PathVariable String segment) {
        // 불러온 ts 파일을 응답으로 전송
        return hlsService.getTsSegment(title, segment)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
