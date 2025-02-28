package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, ChatDto> kafkaTemplate;
    private static final String TOPIC = "chat-messages"; // Kafka 토픽 이름
    /**
     * ChatDto 메시지를 Kafka로 전송
     * @param chatDto 전송할 채팅 메시지 객체
     */
    public void sendMessage(ChatDto chatDto) {
        try {
            kafkaTemplate.send(TOPIC, chatDto); // Kafka로 메시지 전송
        } catch (Exception e) {
            System.err.println("Error sending message to Kafka: " + e.getMessage());
        }
    }
}
