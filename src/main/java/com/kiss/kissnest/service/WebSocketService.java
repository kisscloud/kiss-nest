package com.kiss.kissnest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws")
@Service
@Slf4j
public class WebSocketService {

    private Session session;

    private static CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
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
}
