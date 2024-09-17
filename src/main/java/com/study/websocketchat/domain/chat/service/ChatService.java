package com.study.websocketchat.domain.chat.service;

import com.study.websocketchat.domain.chat.dto.ChatDto;

import java.util.List;

public interface ChatService {

    public List<ChatDto> getDetails(String chatRoomId);

    public boolean addChat(ChatDto chatDto);

    public List<String> getChatRoomList(); // 안 쓰는 메소드
}
