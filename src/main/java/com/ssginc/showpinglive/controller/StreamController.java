package com.ssginc.showpinglive.controller;

import com.ssginc.showpinglive.dto.object.ProductItemDto;
import com.ssginc.showpinglive.dto.request.RegisterStreamRequestDto;
import com.ssginc.showpinglive.dto.request.StreamRequestDto;
import com.ssginc.showpinglive.dto.response.GetStreamRegisterInfoResponseDto;
import com.ssginc.showpinglive.dto.response.StartStreamResponseDto;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.service.ProductService;
import com.ssginc.showpinglive.service.StreamService;
import com.ssginc.showpinglive.service.SubtitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    private final ProductService productService;

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
     * 라이브 방송을 반환해주는 컨트롤러 메서드
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
     * 라이브 중.예정 방송목록을 반환해주는 컨트롤러 메서드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/broadcast")
    public ResponseEntity<?> getBroadCast(@RequestParam(defaultValue = "0", name = "pageNo") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 4);
        Page<StreamResponseDto> pageInfo = streamService.getAllBroadCastByPage(pageable);

        // Map으로 전달할 응답객체 저장
        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 준비중인 방송목록을 반환해주는 컨트롤러 메서드
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/standby/list")
    public ResponseEntity<?> getStandByList(@RequestParam(defaultValue = "0", name = "pageNo") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 4);;
        Page<StreamResponseDto> pageInfo = streamService.getAllStandbyByPage(pageable);

        // Map으로 전달할 응답객체 저장
        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
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
     * 전체 Vod 목록을 반환해주는 컨트롤러 메서드
     * @param categoryNo 카테고리 번호
     * @return 전달할 응답객체 (json 형태로 전달)
     */
    @GetMapping("/vod/category")
    public ResponseEntity<?> getVodListByCategoryAndPage(@RequestParam(name = "categoryNo") Long categoryNo,
                                                         @RequestParam(defaultValue = "0", name = "pageNo") int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 4);
        Page<StreamResponseDto> pageInfo = streamService.getAllVodByCategoryAndPage(categoryNo, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
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

    /**
     * 방송 등록 화면에서 상품 선택을 위해 상품 목록을 반환해주는 메서드
     * @return 상품 목록이 포함된 응답 객체
     */
    @GetMapping("/product/list")
    public ResponseEntity<List<ProductItemDto>> getProductList() {
        List<ProductItemDto> productItemDtoList = productService.getProducts();

        return ResponseEntity.status(HttpStatus.OK).body(productItemDtoList);
    }

    /**
     * 방송 등록을 하는 메서드
     * @param request 방송 등록에 필요한 방송 정보
     * @return 생성 혹은 수정된 방송 데이터의 방송 번호가 포함된 응답 객체
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> createStream(@AuthenticationPrincipal UserDetails userDetails, @RequestBody RegisterStreamRequestDto request) {
        String memberId = null;

        if (userDetails != null) {
            memberId = userDetails.getUsername();
        }

        Long streamNo = streamService.createStream(memberId, request);

        Map<String, Long> response = new HashMap<>();
        response.put("streamNo", streamNo);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 방송 시작을 하는 메서드
     * @param request streamNo가 담긴 요청 객체
     * @return 시작한 방송에 대한 정보
     */
    @PostMapping("/start")
    public ResponseEntity<StartStreamResponseDto> startStream(@RequestBody StreamRequestDto request) {
        StartStreamResponseDto startStreamResponseDto = streamService.startStream(request.getStreamNo());

        return ResponseEntity.status(HttpStatus.OK).body(startStreamResponseDto);
    }

    /**
     * 방송 종료를 하는 메서드
     * @param request streamNo가 담긴 요청 객체
     * @return 방송 종료 설정 적용 여부
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Boolean>> stopStream(@RequestBody StreamRequestDto request) {
        Boolean result = streamService.stopStream(request.getStreamNo());

        Map<String, Boolean> response = new HashMap<>();
        response.put("result", result);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 방송 페이지 이동시 필요한 정보를 가져오는 메서드
     * @param userDetails
     * @return 방송 정보가 담긴 응답 객체
     */
    @GetMapping("/stream")
    public ResponseEntity<GetStreamRegisterInfoResponseDto> getStreamInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String memberId = null;
        if (userDetails != null) {
            memberId = userDetails.getUsername();
        }

        GetStreamRegisterInfoResponseDto response = streamService.getStreamRegisterInfo(memberId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
