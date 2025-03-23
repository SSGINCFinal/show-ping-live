package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;

public interface ChatService {
    ChatDto saveChatMessage(String chatMemberId, Long chatRoomNo, Long chatStreamNo ,String chatMessage, String chatRole, String chatCreatedAt);
}
