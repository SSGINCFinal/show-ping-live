package com.ssginc.showpinglive.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import reactor.core.scheduler.Schedulers;

import java.io.*;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    @Value("${download.path}")
    private String VIDEO_PATH;

    @Value("${ncp.storage.bucketName}")
    private String bucketName;

    private final AmazonS3 amazonS3Client;

    private final StreamRepository streamRepository;

    @Qualifier("webApplicationContext")
    private final ResourceLoader resourceLoader;

    @Override
    public VodResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    /**
     * 영상 제목으로 HLS 파일을 받아오는 메소드
     * @param title 영상 제목
     * @return HLS 파일 (확장자: m3u8)
     */
    @Override
    public Mono<Resource> getHLS(String title) {
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
     * 영상 제목과 segment 번호로 TS 파일을 받아오는 메소드
     * @param title 영상 제목
     * @param segment 세그먼트 번호
     * @return TS 파일 (확장자: ts)
     */
    @Override
    public Mono<Resource> getTsSegment(String title, String segment) {
        return Mono.fromCallable(() ->
            resourceLoader.getResource("file:" + VIDEO_PATH + title + segment + ".ts"));
    }

    @Override
    public String uploadVideo(String title) {
        String filePath = VIDEO_PATH + title;
        File file = new File(filePath);
        String fileName = file.getName();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.length());

        try (InputStream inputStream = new FileInputStream(file)) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

}
