package com.ssginc.showpinglive.service.implement;

import com.ssginc.showpinglive.api.StorageLoader;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.repository.StreamRepository;
import com.ssginc.showpinglive.service.StreamService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StreamServiceImpl implements StreamService {

    @Value("${download.path}")
    private String VIDEO_PATH;

    private final StreamRepository streamRepository;

    private final StorageLoader storageLoader;

    /**
     * 전체 Vod 목록을 반환해주는 메서드
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVod() {
        return streamRepository.findAllVod();
    }

    /**
     * 페이징 정보가 포함된 Vod 목록을 반환해주는 메서드
     * @param pageable 페이징 정보 객체
     * @return 페이징 정보가 있는 vod 목록
     */
    @Override
    public Page<StreamResponseDto> getAllVodByPage(Pageable pageable) {
        return streamRepository.findAllVodByPage(pageable);
    }

    /**
     * 특정 카테고리의 vod 목록을 반환하는 메서드
     * @param categoryNo 카테고리 번호
     * @return vod 목록
     */
    @Override
    public List<StreamResponseDto> getAllVodByCategory(Long categoryNo) {
        return streamRepository.findAllVodByCategory(categoryNo);
    }

    /**
     * 방송중인 라이브 방송 하나를 반환하는 메서드
     * @return 라이브 방송정보 1개
     */
    @Override
    public StreamResponseDto getLive() {
        List<StreamResponseDto> liveList = streamRepository.findLive();
        return liveList.isEmpty() ? null : liveList.get(0);
    }

    /**
     * 영상번호로 VOD 정보를 가져오는 메서드
     * @param streamNo 영상 번호
     * @return 쿼리를 통해 가져온 영상정보 DTO
     */
    @Override
    public StreamResponseDto getVodByNo(Long streamNo) {
        return streamRepository.findVodByNo(streamNo);
    }

    /**
     * VOD 파일을 NCP에 저장하는 메서드
     * @param title 영상 제목
     * @return VOD 저장 링크
     */
    @Override
    public String uploadVideo(String title) {
        String filePath = VIDEO_PATH + title;
        File file = new File(filePath);
        String fileName = file.getName();
        return storageLoader.uploadFile(file, fileName);
    }

}
