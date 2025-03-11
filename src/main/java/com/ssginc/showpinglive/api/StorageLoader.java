package com.ssginc.showpinglive.api;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author dckat
 * NCP Storage로 파일 업로드와 다운로드를 담당하는 클래스
 * <p>
 */
@Component
@RequiredArgsConstructor
public class StorageLoader {

    @Value("${ncp.storage.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3Client;

    /**
     * 제목으로 파일을 NCP에 저장하는 메서드
     * @param file     저장할 파일 대상
     * @param fileName 영상 제목
     */
    public String uploadFile(File file, String fileName) {
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

    public Resource getSubtitle(String fileName) {
        try {
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, fileName));
            return new InputStreamResource(s3Object.getObjectContent());
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("Amazon 서비스 예외 발생: " + e.getMessage(), e);
        }
    }

}
