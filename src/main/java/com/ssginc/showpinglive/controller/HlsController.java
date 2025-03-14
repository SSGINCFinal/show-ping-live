package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;


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
    @GetMapping(value = "/v1/{title}.m3u8")
    public Mono<?> getHLSV1(@PathVariable String title) {
        // 불러온 m3u8 파일을 응답으로 전송
        return hlsService.getHLSV1(title)
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
    @GetMapping(value = "/v1/{title}{segment}.ts")
    public Mono<?> getTsSegmentV1(@PathVariable String title,
                                @PathVariable String segment) {
        // 불러온 ts 파일을 응답으로 전송
        return hlsService.getTsSegmentV1(title, segment)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @PostMapping(value = "/v2/create")
    public ResponseEntity<?> saveHLS(@RequestBody String title) throws IOException, InterruptedException {
        String result = hlsService.saveHLS(title);
        return ResponseEntity.ok(result);
    }

    /**
     * 영상 제목과 segment 번호로 HLS 파일을 NCP Storage에서 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @GetMapping(value = "/v2/{title}.m3u8")
    public Mono<?> getHLSV2(@PathVariable String title) {
        // 불러온 ts 파일을 응답으로 전송
        return hlsService.getHLSV2(title)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 NCP로 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @GetMapping(value = "/v2/{title}{segment}.ts")
    public Mono<?> getTsSegmentV2(@PathVariable String title,
                                  @PathVariable String segment) {
        // 불러온 ts 파일을 응답으로 전송
        return hlsService.getTsSegmentV2(title, segment)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
