package com.ssginc.showpinglive.handler;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class UserCustomHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Principal user = (Principal) attributes.get("user");

        if (user == null) {
            System.err.println("[ERROR] UserCustomHandshakeHandler: Principal is null!");
        } else {
            System.out.println("[DEBUG] WebSocket connection established with Principal: " + user.getName());
        }

        return user;
    }
}
