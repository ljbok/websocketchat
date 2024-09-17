package com.study.websocketchat.domain.chat.service;

import com.study.websocketchat.domain.chat.Chat;
import com.study.websocketchat.domain.chat.dto.ChatDto;
import com.study.websocketchat.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    /**
     * 채팅 기록 없는 경우는 빈 객체 넘겨줌
     * @return List<ChatDto>
     */
    @Override
    public List<ChatDto> getDetails(String chatRoomId) {

        try {
            // List<ChatDto> chatList = chatRepository.findAll().stream().map(chat -> {
            List<ChatDto> chatList = chatRepository.findChatDetails(chatRoomId).stream().map(chat -> {
                return ChatDto.builder()
                            .id(chat.getId().toString())
                            .name(chat.getName())
                            .detail(chat.getDetail())
                            .addDate(chat.getAddDate()).
                            build();
            }).toList();

            if(chatList.isEmpty()) {
                throw new RuntimeException("chat-record is empty");
            }

            return chatList;

        } catch (Exception e) {
            List<ChatDto> emptyChatList = new ArrayList<>();

            log.error(e.getLocalizedMessage());
            return emptyChatList;
        }
    }


    /**
     * 채팅 입력 성공 시 true 실패 시 false 반환
     * @param chatDto
     * @return boolean
     */
    @Override
    public boolean addChat(ChatDto chatDto) {
        try {
            log.info("chatDto -> {}", chatDto);

            Chat chat = chatRepository.save(Chat.dtoToChat(chatDto));
            log.info("chat -> {}", chat);

            if(chat == null) {
                throw new RuntimeException("add chat data is failed");
            }

            return true;

        } catch (Exception e) {

            log.error(e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public List<String> getChatRoomList() { // 안 쓰는 메소드
        try {

            List<String> chatRoomList = chatRepository.findChatRoomIdList();

            if(chatRoomList.isEmpty()) {
                throw new RuntimeException("chatRoomList is null");
            }

            return chatRoomList;

        } catch (Exception e) {

            List<String> emptyRoomList = new ArrayList<>();

            log.error(e.getLocalizedMessage());
            return emptyRoomList;
        }
    }
}
