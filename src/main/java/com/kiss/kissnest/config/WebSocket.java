package com.kiss.kissnest.config;

import com.kiss.kissnest.interceptor.WebSocketHandShakeInterceptor;
import com.kiss.kissnest.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocket implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandShakeInterceptor webSocketHandShakeInterceptor;

    @Autowired
    private WebSocketService webSocketService;

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        return new ServerEndpointExporter();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

        webSocketHandlerRegistry.addHandler(webSocketService, "/ws").addInterceptors(webSocketHandShakeInterceptor).setAllowedOrigins("*");
    }
}
