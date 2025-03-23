package com.ssginc.showpinglive.repository;

import com.ssginc.showpinglive.dto.response.ChatRoomResponseDto;
import com.ssginc.showpinglive.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        SELECT new com.ssginc.showpinglive.dto.response.ChatRoomResponseDto(
            c.chatRoomNo, c.stream.streamNo
        ) FROM ChatRoom c WHERE c.stream.streamNo = :streamNo
    """)
    ChatRoomResponseDto findChatRoomByStreamNo(Long streamNo);

}