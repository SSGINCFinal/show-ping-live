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

/**
 * @author juil1-kim
 * 채팅 메시지 전송 및 브로드캐스트를 담당하는 WebSocket 핸들러 클래스
 * <p>
 */
@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(WebSocketChatHandler.class);
    private final Gson gson = new GsonBuilder().create();

    /**
     * 클라이언트와의 WebSocket 연결이 수립되었을 때 호출되는 메소드
     *
     * @param session 연결된 WebSocket 세션
     * @throws Exception 연결 처리 중 예외 발생 시 던짐
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("채팅 연결됨: " + session.getId());
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출되는 메소드
     * <p>
     * 수신된 메시지를 JSON 객체로 변환하고, 모든 연결된 클라이언트에게 메시지를 브로드캐스트.
     *
     * @param session 메시지를 전송한 클라이언트의 WebSocket 세션
     * @param message 클라이언트로부터 수신한 텍스트 메시지
     * @throws Exception 메시지 처리 중 예외 발생 시 던짐
     */
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

    /**
     * 클라이언트와의 WebSocket 연결이 종료되었을 때 호출되는 메소드
     *
     * @param session 연결이 종료된 WebSocket 세션
     * @param status  연결 종료 상태
     * @throws Exception 연결 종료 처리 중 예외 발생 시 던짐
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.info("채팅 연결 종료됨: " + session.getId());
    }

}
