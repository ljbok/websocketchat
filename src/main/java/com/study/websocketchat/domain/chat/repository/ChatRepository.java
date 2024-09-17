package com.study.websocketchat.domain.chat.repository;

import com.study.websocketchat.domain.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    // 채팅 리스트 전체 출력
    List<Chat> findAll();

    @Query("SELECT c.chatRoomId FROM Chat c GROUP BY c.chatRoomId")
    List<String> findChatRoomIdList();

    // 특정 채팅방의 대화 출력
    @Query("SELECT c FROM Chat c WHERE c.chatRoomId = :chatRoomId ORDER BY c.addDate ASC")
    List<Chat> findChatDetails(@Param("chatRoomId") String chatRoomId);
}
