package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
/**
 * @author juil1-kim
 * Kafka를 이용하여 ChatDto 전송하는 Producer 클래스
 * <p>
 */

public interface KafkaProducerService {
    /**
     * ChatDto 메시지를 Kafka로 전송
     * @param chatDto 전송할 채팅 메시지 객체
     */
    void sendMessage(ChatDto chatDto);
}
