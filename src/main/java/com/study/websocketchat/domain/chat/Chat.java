package com.study.websocketchat.domain.chat;

import com.study.websocketchat.domain.chat.dto.ChatDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 20)
    private String name;

    @Column(name = "DETAIL", length = 255)
    private String detail;

    @Column(name = "ADD_DATE", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false, insertable = false)
    private LocalDateTime addDate;

    @Column(name = "CHAT_ROOM_ID", nullable = false, length = 20)
    private String chatRoomId;

    @Builder
    public Chat(Long id, String name, String detail, LocalDateTime addDate, String chatRoomId) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.addDate = addDate;
        this.chatRoomId = chatRoomId;

    }

    public static Chat dtoToChat(ChatDto chatDto) {
        return Chat.builder()
                .name(chatDto.getName())
                .detail(chatDto.getDetail())
                .chatRoomId(chatDto.getChatRoomId())
                .build();
    }

}
