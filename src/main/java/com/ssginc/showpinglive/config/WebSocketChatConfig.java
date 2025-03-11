package com.ssginc.showpinglive.config;

import com.ssginc.showpinglive.handler.WebSocketChatHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/**
 * @author juil1-kim
 * Websocket을 이용하여 채팅 메세지 브로커 설정 담당 클래스
 * <p>
 * STOMP를 활용하여 Client-Server간의 Websocket 연결 및 메세지 브로커 구성을 수행
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketChatConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * WebSocketChatHandler 빈을 생성하여 WebSocket 메시지 처리 담당.
     *
     * @return WebSocketChatHandler 객체
     */
    @Bean
    public WebSocketChatHandler webSocketChatHandler() {
        return new WebSocketChatHandler();
    }

    /**
     * 메시지 브로커의 구성 설정을 수행하는 메소드
     * <p>
     * 클라이언트로부터의 발행 메시지는 "/pub" prefix를 사용,
     * 서버가 구독하는 클라이언트에는 "/sub" prefix를 사용하도록 설정.
     *
     * @param registry 메시지 브로커 설정을 위한 MessageBrokerRegistry 객체
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub"); // 구독 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 prefix
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * STOMP 엔드포인트를 등록하는 메소드
     * <p>
     * 클라이언트가 WebSocket 연결을 시작할 수 있도록 "/ws-stomp-chat" 엔드포인트를 노출하고,
     * SockJS를 사용하여 브라우저 호환성을 지원.
     *
     * @param registry STOMP 엔드포인트 등록을 위한 StompEndpointRegistry 객체
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp-chat") // WebSocket 연결 Endpoint
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
