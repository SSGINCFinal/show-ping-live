package com.ssginc.showpinglive.service.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssginc.showpinglive.api.Segments;
import com.ssginc.showpinglive.api.StorageLoader;
import com.ssginc.showpinglive.api.SubtitleGenerator;
import com.ssginc.showpinglive.service.SubtitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author dckat
 * 자막과 관련한 로직을 구현한 서비스 클래스
 * <p>
 */
@Service
@RequiredArgsConstructor
public class SubtitleServiceImpl implements SubtitleService {

    private final SubtitleGenerator subtitleGenerator;

    private final StorageLoader storageLoader;

    /**
     * 영상 제목으로 자막 파일을 생성하고 저장하는 메서드
     * @param title 영상 제목
     */
    @Override
    public void createSubtitle(String title) {
        List<Segments> segments = subtitleGenerator.getSubtitles(title);

        String fileName = title + ".json";
        System.out.println(fileName);

        File jsonFile = new File(fileName);
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, segments.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        storageLoader.uploadFile(jsonFile, fileName);
        jsonFile.delete();
    }

}
