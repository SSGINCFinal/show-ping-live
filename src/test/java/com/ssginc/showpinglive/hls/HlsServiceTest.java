package com.ssginc.showpinglive.hls;

import com.ssginc.showpinglive.api.StorageLoader;
import com.ssginc.showpinglive.service.HlsService;
import com.ssginc.showpinglive.service.implement.HlsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.FileNotFoundException;

import static org.mockito.Mockito.*;

public class HlsServiceTest {

    private StorageLoader storageLoader; // StorageLoader에 대한 목(mock)

    private HlsService hlsService;       // 인터페이스 타입으로 참조

    private ResourceLoader resourceLoader;

    @BeforeEach
    public void setUp() {
        storageLoader = mock(StorageLoader.class);
        // HlsServiceImpl은 HlsService 인터페이스의 구현체입니다.
        hlsService = new HlsServiceImpl(resourceLoader, storageLoader);
    }

    @Test
    public void testGetHLSV2() {
        // given
        String title = "sampleVideo";
        String expectedFileName = title + ".m3u8";
        byte[] dummyContent = "dummy m3u8 content".getBytes();
        Resource resource = new ByteArrayResource(dummyContent);
        when(storageLoader.getHLS(expectedFileName)).thenReturn(resource);

        // when
        Mono<Resource> resultMono = (Mono<Resource>) hlsService.getHLSV2(title);

        // then
        StepVerifier.create(resultMono)
                .expectNextMatches(res -> {
                    try {
                        return new String(res.getInputStream().readAllBytes())
                                .equals(new String(dummyContent));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .verifyComplete();

        verify(storageLoader, times(1)).getHLS(expectedFileName);
    }

    @Test
    public void testGetTsSegmentV2() {
        // given
        String title = "sampleVideo";
        String segment = "1";
        String expectedFileName = title + segment + ".ts";
        byte[] dummyContent = "dummy ts content".getBytes();
        Resource resource = new ByteArrayResource(dummyContent);
        when(storageLoader.getHLS(expectedFileName)).thenReturn(resource);

        // when
        Mono<?> resultMono = hlsService.getTsSegmentV2(title, segment);

        // then
        StepVerifier.create(resultMono)
                .expectNextMatches(res -> {
                    try {
                        return new String(((Resource) res).getInputStream().readAllBytes())
                                .equals(new String(dummyContent));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .verifyComplete();

        verify(storageLoader, times(1)).getHLS(expectedFileName);
    }

    @Test
    public void testGetHLSV2_FileNotFound() {
        // given: 존재하지 않는 파일의 경우 FileNotFoundException을 던지도록 설정
        String title = "nonExistentVideo";
        String expectedFileName = title + ".m3u8";

        // when
        Mono<Resource> resultMono = (Mono<Resource>) hlsService.getHLSV2(title);

        StepVerifier.create(resultMono)
                .expectNextCount(0)  // 데이터가 없음을 확인
                .verifyComplete();

        verify(storageLoader, times(1)).getHLS(expectedFileName);
    }

    @Test
    public void testGetTsSegmentV2_FileNotFound() {
        // given
        String title = "nonExistentVideo";
        String segment = "1";
        String expectedFileName = title + segment + ".ts";

        // when
        Mono<?> resultMono = hlsService.getTsSegmentV2(title, segment);

        // then
        StepVerifier.create(resultMono)
                .expectNextCount(0)  // 데이터가 없음을 확인
                .verifyComplete();

        verify(storageLoader, times(1)).getHLS(expectedFileName);
    }
}