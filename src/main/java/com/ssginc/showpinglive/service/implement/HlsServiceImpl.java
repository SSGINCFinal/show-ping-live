package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.service.HlsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;

@Service
@RequiredArgsConstructor
public class HlsServiceImpl implements HlsService {

    @Value("${download.path}")
    private String VIDEO_PATH;

    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;

    /**
     * 영상 제목으로 HLS 파일을 받아오는 메서드
     * @param title 영상 제목
     * @return HLS 파일 (확장자: m3u8)
     */
    @Override
    public Mono<?> getHLS(String title) {
        return Mono.fromCallable(() -> {
            File inputFile = new File(VIDEO_PATH, title + ".mp4");
            File outputFile = new File(VIDEO_PATH, title + ".m3u8");

            // FFmpeg를 사용하여 HLS로 변환
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-codec:", "copy", "-start_number", "0",
                    "-hls_time", "10", "-hls_list_size", "0",
                    "-f", "hls", outputFile.getAbsolutePath()
            );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("FFmpeg 변환 실패. Exit code: " + exitCode);
            }

            // 변환된 m3u8 파일을 Resource로 반환
            return resourceLoader.getResource("file:" + outputFile.getAbsolutePath());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * 영상 제목과 segment 번호로 TS 파일을 받아오는 메서드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일 (확장자: ts)
     */
    @Override
    public Mono<?> getTsSegment(String title, String segment) {
        return Mono.fromCallable(() ->
                resourceLoader.getResource("file:" + VIDEO_PATH + title + segment + ".ts"));
    }

}
