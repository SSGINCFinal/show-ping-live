package com.ssginc.showpinglive;


import com.ssginc.showpinglive.controller.StreamController;
import com.ssginc.showpinglive.dto.response.StreamResponseDto;
import com.ssginc.showpinglive.jwt.JwtUtil;
import com.ssginc.showpinglive.service.StreamService;
import com.ssginc.showpinglive.service.SubtitleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StreamController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StreamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 실제 streamService 빈 이름과 타입으로 변경
    @MockBean
    private StreamService streamService;

    // 실제 streamService 빈 이름과 타입으로 변경
    @MockBean
    private SubtitleService subtitleService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testGetVodListByPage() throws Exception {
        // 테스트용 더미 데이터 생성 (StreamResponseDto 클래스의 생성자 및 필드를 실제에 맞게 채워주세요)
        List<StreamResponseDto> dtoList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            StreamResponseDto dto = new StreamResponseDto();
            dtoList.add(dto);
        }

        int totalPage = dtoList.size() / 4;

        for (int i = 0; i < totalPage; i++) {
            // streamService가 호출되면 위 pageInfo를 반환하도록 stubbing
            Pageable pageable = PageRequest.of(i, 4);
            Page<StreamResponseDto> pageInfo = new PageImpl<>(dtoList, pageable, dtoList.size());
            when(streamService.getAllVodByPage(any(Pageable.class))).thenReturn(pageInfo);

            // GET 요청 전송 후 응답 검증
            mockMvc.perform(get("/stream/vod/list/page")
                            .param("pageNo", String.valueOf(i)))
                    .andExpect(status().isOk())
                    // "pageInfo" 내부의 "content" 배열의 크기가 더미 데이터 개수와 일치하는지 확인
                    .andExpect(jsonPath("$.pageInfo.content.length()").value(dtoList.size()))
                    // 추가적으로 페이지 관련 정보(예: totalElements, number 등)를 검증할 수 있습니다.
                    .andExpect(jsonPath("$.pageInfo.number").value(i));
        }
    }

}
