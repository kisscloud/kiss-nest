package com.kiss.kissnest.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.*;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;


@Service
@Slf4j
@RequestMapping("/ws")
public class WebSocketService implements WebSocketHandler {

    private Integer teamId;


    private static final List<WebSocketSession> sessionMap = new ArrayList<>();


    public void broadcastTeamMessage(TextMessage message) {
        for (WebSocketSession session : sessionMap) {
            System.out.println("session=" + session);
            if (session.isOpen() && session.getAttributes().get("teamId").equals(1)) {
                // 发送消息
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        teamId = (Integer) webSocketSession.getAttributes().get("teamId");
        System.out.println("前端发送的消息=" + webSocketMessage.toString());
        sessionMap.add(webSocketSession);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        sessionMap.remove(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        System.out.println("关闭链接");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
