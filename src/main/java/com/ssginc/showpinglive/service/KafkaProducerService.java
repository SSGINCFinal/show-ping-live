package com.ssginc.showpinglive.service;
import com.ssginc.showpinglive.dto.object.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, ChatDto> kafkaTemplate;

    public void send(String topic, ChatDto message) {
        kafkaTemplate.send(topic, message);
    }
}