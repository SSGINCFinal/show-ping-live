package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.GetStreamRegisterInfoDto;
import com.ssginc.showpinglive.dto.request.RegisterStreamRequestDto;
import com.ssginc.showpinglive.dto.response.GetStreamRegisterInfoResponseDto;
import com.ssginc.showpinglive.dto.response.StartStreamResponseDto;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface StreamService {

    List<StreamResponseDto> getAllVod();

    Page<StreamResponseDto> getAllVodByPage(Pageable pageable);

    List<StreamResponseDto> getAllVodByCategory(Long categoryNo);

    StreamResponseDto getLive();

    StreamResponseDto getVodByNo(Long streamNo);

    Mono<Resource> getHLS(String title);

    Mono<Resource> getTsSegment(String title, String segment);

    String uploadVideo(String filePath);

    GetStreamRegisterInfoResponseDto getStreamRegisterInfo(String memberId);

    Long createStream(String memberId, RegisterStreamRequestDto request);

    StartStreamResponseDto startStream(Long streamNo);

    Boolean stopStream(Long streamNo);

}
