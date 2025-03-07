package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.dto.object.ForbiddenWord;
import com.ssginc.showpinglive.repository.ChatRepository;
import com.ssginc.showpinglive.repository.ForbiddenWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ForbiddenWordRepository forbiddenWordRepository;
    private final KafkaProducerService kafkaProducerService;

    public ChatDto saveChatMessage(String chatMemberId, Long chatRoomNo, String chatMessage, String chatCreatedAt) {
        // 1. 금칙어 필터링 로직
        if (isForbiddenWordIncluded(chatMessage)) {
            throw new IllegalArgumentException("금칙어가 포함된 메시지는 전송할 수 없습니다.");
        }

        // 2. 채팅 메시지 저장
        ChatDto message = new ChatDto();
        message.setChatMemberId(chatMemberId);
        message.setChatRoomNo(chatRoomNo);
        message.setChatMessage(chatMessage);

        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        LocalDateTime nowKST = LocalDateTime.now(seoulZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(nowKST);
        message.setChatCreatedAt(dateString);

        ChatDto savedMessage = chatRepository.save(message);

        // 2) Kafka 전송
        kafkaProducerService.sendMessage(savedMessage);
        System.out.println("[DEBUG] KafkaProducerService.sendMessage() 호출 완료. chatRoomNo=" + savedMessage.getChatRoomNo());


        return savedMessage; // MongoDB에 저장
    }

    // 금칙어 필터링 로직
    private boolean isForbiddenWordIncluded(String message) {
        List<ForbiddenWord> forbiddenWords = forbiddenWordRepository.findAll();
        for (ForbiddenWord slang : forbiddenWords) {
            if (message.contains(slang.getWord())) {
                return true; // 금칙어가 포함된 경우
            }
        }
        return false; // 금칙어가 없는 경우
    }

    public List<ChatDto> findMessagesByRoom(Long chatRoomNo) {
        return chatRepository.findByChatRoomNo(chatRoomNo);
    }
}
