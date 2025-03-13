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
     * @return 업로드된 파일 링크
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

    /**
     * 생성된 HLS 파일과 TS를 NCP Storage에 저장하는 메서드
     * @param files    저장할 파일 리스트
     * @return 업로드된 파일 링크
     */
    public String uploadHlsFiles(File[] files, String fileName) {
        if (files != null) {
            for (File file : files) {
                String remoteKey = "video/hls/" + file.getName();
                amazonS3Client.putObject(new PutObjectRequest(bucketName, remoteKey, file));
            }
        }
        return "video/hls/" + fileName + ".m3u8";
    }

    /**
     * 지정된 파일 이름으로 NCP Storage json 자막파일 불러오는 메서드
     * @param fileName 영상 제목
     * @return 자막 json 파일
     */
    public Resource getSubtitle(String fileName) {
        try {
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, fileName));
            return new InputStreamResource(s3Object.getObjectContent());
        } catch (AmazonS3Exception e) {
            throw new RuntimeException("Amazon 서비스 예외 발생: " + e.getMessage(), e);
        }
    }

    /**
     * 지정된 파일 이름으로 NCP Storage에서 HLS 파일들을 불러오는 메서드
     * @param fileName 영상 제목
     * @return 불러온 리소스
     */
    public Resource getHLS(String fileName) {
        String remoteKey = "video/hls/" + fileName;
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, remoteKey));
        return new InputStreamResource(s3Object.getObjectContent());
    }

}
