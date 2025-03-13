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
     * 라이브 메인 페이지 요청 컨틀롤러 메서드
     * @return 라이브 메인 페이지 (타임리프)
     */
    @GetMapping("/list")
    public String streamList() {
        return "stream/list";
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메서드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/live")
    public ResponseEntity<?> getLive() {
        StreamResponseDto live = streamService.getLive();

        // Map으로 전달할 응답객체 저장
        Map<String, Object> result = new HashMap<>();
        result.put("live", live);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메서드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list")
    public ResponseEntity<?> getVodList() {
        List<StreamResponseDto> vodList = streamService.getAllVod();

        // Map으로 전달할 응답객체 저장
        Map<String, Object> result = new HashMap<>();
        result.put("vodList", vodList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메서드
     * @param pageNo 요청한 페이지 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list/page")
    public ResponseEntity<?> getVodListByPage(@RequestParam(defaultValue = "0", name = "pageNo") int pageNo) {
        // 페이지 당 불러올 객체 단위 지정 (4개)
        Pageable pageable = PageRequest.of(pageNo, 4);
        Page<StreamResponseDto> pageInfo = streamService.getAllVodByPage(pageable);

        // Map으로 전달할 응답객체 저장
        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 전체 Vod 목록을 반환해주는 컨트롤러 메서드
     * @param categoryNo 카테고리 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/list/{categoryNo}")
    public ResponseEntity<?> getVodListByCategory(@PathVariable Long categoryNo) {
        List<StreamResponseDto> vodList = streamService.getAllVodByCategory(categoryNo);
        Map<String, Object> result = new HashMap<>();

        result.put("vodList", vodList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * VOD 파일을 NCP Storage에 저장을 요청하는 컨트롤러 메서드
     * @param requestData 요청 데이터 정보
     * @return VOD의 저장결과 응답객체
     */
    @PostMapping("/vod/upload")
    public ResponseEntity<?> uploadVod(@RequestBody Map<String, String> requestData) {
        String title = requestData.get("title");
        return ResponseEntity.ok(streamService.uploadVideo(title));
    }

    /**
     * 영상 제목으로 자막 생성하는 컨트롤러 메서드
     * @param title 영상 제목
     * @return 자막 생성 여부 응답 객체
     */
    @PostMapping("/subtitle/create")
    public ResponseEntity<?> createSubtitle(@RequestBody String title) {
        subtitleService.createSubtitle(title);
        return ResponseEntity.ok()
                .body("자막 생성 성공");
    }

    /**
     * 파일 제목으로 자막 정보 파일을 가져오는 메서드
     * @param title 파일 제목
     * @return 자막 생성 여부 응답 객체
     */
    @GetMapping("/subtitle/{title}.json")
    public ResponseEntity<?> getSubtitle(@PathVariable String title) {
        Resource subtitleJson = subtitleService.getSubtitle(title);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(subtitleJson);
    }

}
