package com.ssginc.showpinglive.service;

import com.ssginc.showpinglive.dto.object.ChatDto;
import java.util.List;

public interface ChatService {

    ChatDto saveChatMessage(String chatMemberId, Long chatRoomNo, String chatMessage, String chatRole, String chatCreatedAt);

}
