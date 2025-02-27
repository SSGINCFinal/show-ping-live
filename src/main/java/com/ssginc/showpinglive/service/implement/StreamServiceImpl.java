package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.dto.response.VodResponseDto;
import com.ssginc.showpinglive.repository.StreamRepository;
import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    @Value("${download.path}")
    private String VIDEO_PATH;

    private final StreamRepository streamRepository;

    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;

    @Override
    public VodResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    @Override
    public Mono<Resource> getVideo(String title) {
        String filePath = VIDEO_PATH + title + ".mp4";
        return Mono.fromSupplier(() ->
                resourceLoader.getResource(String.format(filePath)));
    }

}
