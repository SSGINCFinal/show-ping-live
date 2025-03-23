package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    @CrossOrigin(origins = "https://showping.duckdns.org")
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
    @CrossOrigin(origins = "https://showping.duckdns.org")
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
     * 영상 제목과 segment 번호로 HLS 파일을 NCP Storage에서 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @CrossOrigin(origins = "https://showping.duckdns.org")
    @GetMapping(value = "/v2/flux/{title}.m3u8")
    public Mono<?> getHLSV2Flux(@PathVariable String title) {
        // 불러온 ts 파일을 응답으로 전송
        return hlsService.getHLSV2Flux(title)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 NCP로 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일이 있는 응답객체 (확장자: ts)
     */
    @CrossOrigin(origins = "https://showping.duckdns.org")
    @GetMapping(value = "/v2/flux/{title}{segment}.ts")
    public Mono<?> getTsSegmentV2Flux(@PathVariable String title,
                                  @PathVariable String segment) {
        // 불러온 ts 파일을 응답으로 전송
        return hlsService.getTsSegmentV2Flux(title, segment)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                        .body(resource))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * 영상 제목으로 HLS 파일(m3u8)을 NCP Storage에서 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @return TS 파일을 포함한 응답 객체 (확장자: ts)
     */
    @CrossOrigin(origins = "https://showping.duckdns.org")
    @GetMapping(value = "/v2/{title}.m3u8")
    public ResponseEntity<?> getHLSV2(@PathVariable String title) {
        // 동기 방식으로 리소스를 가져옵니다.
        Resource resource = hlsService.getHLSV2(title);
        if (resource != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.mpegurl")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 NCP Storage에서 받아오는 컨트롤러 메서드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일을 포함한 응답 객체 (확장자: ts)
     */
    @CrossOrigin(origins = "https://showping.duckdns.org")
    @GetMapping(value = "/v2/{title}{segment}.ts")
    public ResponseEntity<?> getTsSegmentV2(@PathVariable String title,
                                            @PathVariable String segment) {
        // 동기 방식으로 리소스를 가져옵니다.
        Resource resource = hlsService.getTsSegmentV2(title, segment);
        if (resource != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp2t")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
