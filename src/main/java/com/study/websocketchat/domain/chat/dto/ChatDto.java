package com.study.websocketchat.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 단순 테스트 dto 이므로 request 와 response를 구분하지 않겠다.
 */
@ToString
@Getter
@NoArgsConstructor
public class ChatDto {

    private String id;
    private String name;
    private String detail;
    private LocalDateTime addDate;
    private String chatRoomId;

    @Builder
    public ChatDto(String id, String name, String detail, LocalDateTime addDate, String chatRoomId) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.addDate = addDate;
        this.chatRoomId = chatRoomId;
    }
}
