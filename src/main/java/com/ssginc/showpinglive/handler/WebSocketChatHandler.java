package com.ssginc.showpinglive.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(WebSocketChatHandler.class);
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("채팅 연결됨: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 클라이언트로부터 받은 메시지를 JSON 객체로 변환
        JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
        log.info("수신된 채팅 메시지: " + jsonMessage);

        // 모든 연결된 클라이언트에게 메시지 브로드캐스트 (메시지는 JSON 문자열 그대로 전송)
        for (WebSocketSession client : sessions.values()) {
            if (client.isOpen()) {
                try {
                    client.sendMessage(new TextMessage(jsonMessage.toString()));
                } catch (IOException e) {
                    log.error("메시지 전송 실패: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("채팅 연결 종료됨: " + session.getId());
    }

    private void sendError(WebSocketSession session, String errorMsg) {
        JsonObject response = new JsonObject();
        response.addProperty("error", errorMsg);
        try {
            session.sendMessage(new TextMessage(response.toString()));
        } catch (IOException e) {
            log.error("에러 메시지 전송 실패: " + e.getMessage());
        }
    }
}
