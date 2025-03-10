package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.service.StreamService;
import com.ssginc.showpinglive.service.SubtitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dckat
 * 영상과 관련한 요청-응답을 수행하는 컨트롤러 클래스
 * <p>
 */
@Controller
@RequestMapping("stream")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    private final SubtitleService subtitleService;

    /**
     * 라이브 메인 페이지 요청 컨틀롤러 메소드
     * @return 라이브 메인 페이지 (타임리프)
     */
    @GetMapping("/list")
    public String streamList() {
        return "stream/list";
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> getLive() {
        StreamResponseDto live = streamService.getLive();
        Map<String, Object> result = new HashMap<>();

        result.put("live", live);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list")
    public ResponseEntity<Map<String, Object>> getVodList() {
        List<StreamResponseDto> vodList = streamService.getAllVod();
        Map<String, Object> result = new HashMap<>();

        result.put("vodList", vodList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @param pageNo 요청한 페이지 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list/page")
    public ResponseEntity<Map<String, Object>> getVodListByPage(@RequestParam(defaultValue = "0", name = "pageNo") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 4);
        Page<StreamResponseDto> pageInfo = streamService.getAllVodByPage(pageable);
        Map<String, Object> result = new HashMap<>();

        result.put("pageInfo", pageInfo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메소드
     * @param categoryNo 카테고리 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list/{categoryNo}")
    public ResponseEntity<Map<String, Object>> getVodListByCategory(@PathVariable Long categoryNo) {
        List<StreamResponseDto> vodList = streamService.getAllVodByCategory(categoryNo);
        Map<String, Object> result = new HashMap<>();

        result.put("vodList", vodList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

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

    /**
     * 영상 제목으로 자막 생성하는 컨트롤러 메소드
     * @param title 영상 제목
     * @return 자막 생성 여부 응답 객체
     */
    @PostMapping("/subtitle/create")
    public ResponseEntity<?> createSubtitle(@RequestBody String title) {
        subtitleService.createSubtitle(title);
        return ResponseEntity.ok()
                .body("자막 생성 성공");
    }

}
