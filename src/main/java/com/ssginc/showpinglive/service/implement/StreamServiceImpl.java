package com.ssginc.showpinglive.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
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

import java.io.*;
import java.util.UUID;

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

    @Override
    public Mono<Resource> getVideo(String title) {
        String fullPath = amazonS3Client.getUrl(bucketName, title + ".mp4").toString();
        System.out.println(fullPath);
        return Mono.fromSupplier(() ->
                resourceLoader.getResource(String.format(fullPath)));
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
