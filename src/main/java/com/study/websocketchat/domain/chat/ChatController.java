package com.study.websocketchat.domain.chat;

import com.study.websocketchat.domain.chat.dto.ChatDto;
import com.study.websocketchat.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    // 채팅방 최초 입장 시
    @PostMapping("/join/chatRoom")
    public List<ChatDto> joinChatRoomInit(@RequestBody ChatDto chatDto) {
        log.info("ChatController is matchaing : joinChatRoomInit");
        log.info(chatService.getDetails(chatDto.getChatRoomId()).toString());
        if(chatService.getDetails(chatDto.getChatRoomId()) == null || chatService.getDetails(chatDto.getChatRoomId()).isEmpty()) {
            return new ArrayList<>();
        } else {
            return chatService.getDetails(chatDto.getChatRoomId());
        }
    }
}
