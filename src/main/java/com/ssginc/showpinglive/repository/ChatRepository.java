package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.dto.object.ChatDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<ChatDto, String> {
    List<ChatDto> findByChatStreamNo(Long chatStreamNo);
}