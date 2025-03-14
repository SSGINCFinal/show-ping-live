package com.ssginc.showpinglive.config;

import com.ssginc.showpinglive.handler.WebSocketChatHandler;
import com.ssginc.showpinglive.handler.LiveHandler;
import com.ssginc.showpinglive.handler.RecordHandler;
import com.ssginc.showpinglive.util.UserRegistry;
import org.kurento.client.KurentoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;


/**
 * @author dckat
 * 웹소켓 설정 클래스
 * <p>
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

//    @Bean
//    public LiveHandler liveHandler() {
//        return new LiveHandler();
//    }
//
//    @Bean
//    public RecordHandler recordHandler() {
//        return new RecordHandler();
//    }
//
//    @Bean
//    public WebSocketChatHandler webSocketChatHandler() {return new WebSocketChatHandler();}

    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create("ws://rapunzel.iptime.org:7000/kurento");
    }

    @Bean
    public UserRegistry registry() {
        return new UserRegistry();
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketChatHandler(), "/chat");
        registry.addHandler(new LiveHandler(), "/live");
        registry.addHandler(new RecordHandler(), "/record");
    }

}
