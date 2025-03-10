package com.ssginc.showpinglive.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author dckat
 * clova speech API를 통해 자막정보를 받아오는 클래스
 * <p>
 */
@Component
public class SubtitleGenerator {

    @Value("${ncp.clova-speech.api-url}")
    private String API_URL;

    @Value("${ncp.clova-speech.path}")
    private String DOMAIN;

    @Value("${ncp.clova-speech.api-key}")
    private String API_KEY;

    /**
     * 영상 제목으로 자막 정보 리스트를 생성하는 메서드
     * @param title 영상 제목
     * @return 자막 정보 리스트
     */
    public List<Segments> getSubtitles(String title) {
        List<Segments> segments = new ArrayList<>();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // dataKey 생성
            String dataKey = DOMAIN + "/" + title + ".mp4";
            System.out.println(dataKey);

            // API 호출을 위한 POST 요청 생성
            HttpPost postRequest = new HttpPost(API_URL);

            // 인증 헤더 추가
            postRequest.addHeader("X-CLOVASPEECH-API-KEY", API_KEY);
            postRequest.addHeader("Content-Type", "application/json");

            // 요청 body 생성
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("dataKey", dataKey);
            requestBody.put("language", "ko-KR");
            requestBody.put("completion", "sync");
            requestBody.put("fullText", true);

            StringEntity requestEntity = new StringEntity(requestBody.toString(), "UTF-8");
            postRequest.setEntity(requestEntity);

            // API 호출 및 응답 처리
            HttpResponse response = httpClient.execute(postRequest);
            HttpEntity responseEntity = response.getEntity();
            String jsonResponse = EntityUtils.toString(responseEntity);
            System.out.println("API 응답: " + jsonResponse);

            // ObjectMapper 생성
            ObjectMapper mapper = new ObjectMapper();

            // 문자열을 JsonNode로 변환
            JsonNode rootNode = mapper.readTree(jsonResponse);

            // 예시: segments 배열 추출
            JsonNode segmentsNode = rootNode.path("segments");

            for (JsonNode segment : segmentsNode) {
                segments.add(Segments.builder()
                        .start(segment.get("start").asLong())
                        .end(segment.get("end").asLong())
                        .text(segment.get("text").asText())
                        .build());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return segments;
    }

}
