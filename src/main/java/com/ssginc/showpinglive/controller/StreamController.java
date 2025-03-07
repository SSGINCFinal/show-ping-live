package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@RequestMapping("stream")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    /**
     * 영상 제목으로 HLS 파일을 받아오는 컨트롤러 메소드
     * @param title 영상 제목
     * @return HLS 파일이 포함된 응답객체 (확장자: m3u8)
     */
    @GetMapping(value = "/vod/{title}.m3u8")
    public Mono<ResponseEntity<Resource>> getHLS(@PathVariable String title) {
        return streamService.getHLS(title)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE,
                                "application/vnd.apple.mpegurl")
                        .body(resource));
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 받아오는 컨트롤러 메소드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @GetMapping(value = "/vod/{title}{segment}.ts")
    public Mono<ResponseEntity<Resource>> getTsSegment(@PathVariable String title,
                                                       @PathVariable String segment) {
        return streamService.getTsSegment(title, segment)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/vod/upload")
    public ResponseEntity<String> uploadVod(@RequestBody Map<String, String> requestData) {
        System.out.println(requestData);
        String title = requestData.get("title");
        System.out.println(title);
        return ResponseEntity.ok(streamService.uploadVideo(title));
    }
}
