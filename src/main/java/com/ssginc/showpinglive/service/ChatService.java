package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;
import com.ssginc.showpinglive.dto.object.ForbiddenWord;
import com.ssginc.showpinglive.repository.ChatRepository;
import com.ssginc.showpinglive.repository.ForbiddenWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository; // 변수명 통일 (chatMessageRepository -> chatRepository)
    private final ForbiddenWordRepository forbiddenWordRepository;

    public ChatDto saveChatMessage(Long chatStreamNo, Long chatMemberNo, Long chatRoomNo, String chatMessage) {
        // 1. 금칙어 필터링 로직
        if (isForbiddenWordIncluded(chatMessage)) {
            throw new IllegalArgumentException("금칙어가 포함된 메시지는 전송할 수 없습니다.");
        }

        // 2. 채팅 메시지 저장
        ChatDto message = new ChatDto();
        message.setChatStreamNo(chatStreamNo);
        message.setChatMemberNo(chatMemberNo);
        message.setChatRoomNo(chatRoomNo);
        message.setChatMessage(chatMessage);
        message.setChatCreatedAt(LocalDateTime.now());

        return chatRepository.save(message); // MongoDB에 저장
    }

    // 금칙어 필터링 로직을 별도 메서드로 분리
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
