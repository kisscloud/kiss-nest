package com.kiss.kissnest.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CopyOnWriteArraySet;


@Service
@Slf4j
@RequestMapping("/ws")
public class WebSocketService implements WebSocketHandler {

    private Integer teamId;

    private Session session;

    private static CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        URI uri = session.getRequestURI();
        this.session = session;
        webSocketSet.add(this);
        log.info("新连接加入");
    }

    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("连接断开");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到消息：{}", message);
        this.session.getBasicRemote().sendText("{ ping: ts }");
    }

    public void broadcastTeamMessage() {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {

    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        System.out.println("前端发送的消息=" + webSocketMessage.toString());
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        webSocketSet.remove(webSocketSession);
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
