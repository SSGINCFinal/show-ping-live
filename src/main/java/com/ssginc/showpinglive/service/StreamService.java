package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.response.VodResponseDto;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;

public interface StreamService {

    VodResponseDto getVodByNo(Long streamNo);

    Mono<Resource> getHLS(String title);

    Mono<Resource> getTsSegment(String title, String segment);

    String uploadVideo(String filePath);

}
