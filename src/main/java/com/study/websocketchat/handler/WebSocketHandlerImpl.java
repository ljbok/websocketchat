package com.study.websocketchat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.websocketchat.domain.chat.dto.ChatDto;
import com.study.websocketchat.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    // 채팅 서비스 빈 주입
    private final ChatService chatService;
    
    private final Set<WebSocketSession> sessions = new HashSet<>(); // 각 사용자마다 하나의 고유 세션이 그 세션들을 전부 가지고 있는 객체

    // chatRoomId: {session1, session2}  --> 사용자들의 session 세션이 들어가게 됨 예시는 두 명의 사용자 가정
    private final Map<String, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();
    
    
    // 웹소켓 연결 성공 시
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // super.afterConnectionEstablished(session);
        log.info("session connect is success by session_id : " + session.getId());
        if (!sessions.contains(session)) {
            sessions.add(session); // 모든 사용자들의 세션을 모아놓는 sesstions 객체에 신규 사용자 세션 추가
        }
    }

    // # 현재 프로젝트 에서는 다루지 않음
    //    @Override
    //    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    //        super.handleMessage(session, message);
    //    }
    
    // 웹소켓 통신시 메시지 다루는 부분
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // super.handleTextMessage(session, message);
        String payLoad = message.getPayload();
        log.info("payLoad : " + payLoad);

        // 페이로드(입력받은 메시지) -> chatMessageDto로 변환
        ChatDto chatDto = objectMapper.readValue(payLoad, ChatDto.class);
        log.info("message from session -> {}", chatDto);

        String chatRoomId = chatDto.getChatRoomId();


        if(!chatRoomSessionMap.containsKey(chatRoomId)) { // 해당 채팅이 채팅세션맵에 존재하지 않는다면
            chatRoomSessionMap.put(chatRoomId, new HashSet<>());
        }

        if(!chatRoomSessionMap.get(chatRoomId).contains(session)) { // 해상 사용자아 해당 채팅방세션맵의 채팅방세션정보에 없다면
            chatRoomSessionMap.get(chatRoomId).add(session);
        }

        Set<WebSocketSession> chatRoomSessions = chatRoomSessionMap.get(chatRoomId);

        if (chatRoomSessions != null) {
            // 채팅 메시지를 session 에 반환하면서 db 에 적재하는 커스텀 메소드
            customSendMessage(chatRoomSessions, chatDto, message);
        } else {
            log.error("there is no chat Info by chatRoomId : " + chatRoomId);
            throw new RuntimeException("채팅방 정보를 찾을 수 없습니다.");
        }
    }
    
    // # pong 타입 메시지는 다루지 않음 기본 타입인 ping 타입 데이터를 다룸  
    //    @Override
    //    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    //        super.handlePongMessage(session, message);
    //    }
    
    // # 현재 프로젝트에서 사용하지 않으
    //    @Override
    //    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    //        // super.handleTransportError(session, exception);
    //    }

    // 웹소켓 연결 종료 시 처리
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {


        // 1. 클라이언트가 채팅방을 떠났을 때 처리해야 할 로직
        // 예를 들어, 채팅방 목록에서 사용자를 제거하고 퇴장 메시지 브로드캐스트
        // 2. 다른 사용자들에게 사용자가 나갔음을 알릴 수 있음
        // 예시: 모든 사용자에게 "사용자가 채팅방을 떠났습니다." 메시지 전송

        String targetChatRoomId = null;
        Set<WebSocketSession> targetSessions = null;

        TextMessage userExitMessage = new TextMessage("{" +
                "id:'0'" +
                "user:'관리자'" +
                "detail:" + "'" + session.getId() + " 님께서 퇴장하였습니다." + "'" +
                "user:'알림'" +
                "chatRoomId:" + "'" + targetChatRoomId + "'" +
                "addTime:" + "'" + new Date().toString() + "'" +
                "}");

        try {

            log.info(session.getId() + "-session is closed");

            for (String chatRoomId : chatRoomSessionMap.keySet()) {

                if (chatRoomSessionMap.get(chatRoomId).contains(session)) {

                    targetChatRoomId = chatRoomId;
                    targetSessions = chatRoomSessionMap.get(chatRoomId);

                    chatRoomSessionMap.get(chatRoomId).remove(session);
                    log.info(session.getId() + "-session is deleted in chatRoomId : " + chatRoomId);
                }

                if(targetSessions == null || targetSessions.isEmpty()) {
                    chatRoomSessionMap.remove(chatRoomId);
                    log.info( chatRoomId + "-chatRoom is deleted by this chatRoom user is all out or not exist");
                }
            }

            // 전체 사용자 세션들에서 해당 사용자의 세션 삭제
            if (sessions.removeIf(sessionItem -> sessionItem.getId().equals(session.getId()))) {
                log.info("user session-id(" +  session.getId() + ") id deleted successfully");
            }

            super.afterConnectionClosed(session, status);

        } catch(Exception e) { // 예외 발생 시 웹소켓 서버 강제 종료 되길래 방지하고자 throw 대신 예외 메시지 리턴
            // throw new RuntimeException("WebSocket session closing is failed because :" + e.getLocalizedMessage());
            customSendError(session, e);
        }


    }
    
    // 사용하지 않음
    //    @Override
    //    public boolean supportsPartialMessages() {
    //        return super.supportsPartialMessages();
    //    }


    // 채팅 관련 메소드 커스텀 메소드 ======
    // 채팅 메시지를 session 에 반환하면서 db 에 적재하는 커스텀 메소드
    public void customSendMessage(Set<WebSocketSession> chatRoomSessions, ChatDto chatDto, TextMessage message) {
        try{
            if(chatDto != null) {
                chatService.addChat(chatDto); // db 적재
            }

            for(WebSocketSession session : chatRoomSessions) {
                session.sendMessage(message); // 사용자에게 세션으로 반환
            }
        } catch (Exception e) {
            log.error("send message to chatRoom is faisle because : ");
            log.error(e.getLocalizedMessage());
        }
    }

    public void customSendError(WebSocketSession session, Exception e) {
        TextMessage errorTextMessage = new TextMessage("{" +
                "id:'-1'" +
                "user:'관리자'" +
                "detail: 메시지 전송에 실패하였습니다. 사유 : " + e.getLocalizedMessage() +
                "user:'알림'" +
                "addTime:" + "'" + new Date().toString() + "'" +
                "}");
        log.error(e.getLocalizedMessage());
        chatRoomSessionMap.keySet().forEach(key -> {
            if (chatRoomSessionMap.get(key).contains(session)) {
                customSendMessage(chatRoomSessionMap.get(key), null ,errorTextMessage);
            };
        });
    }
}
