package com.kiss.kissnest.service;


import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.output.MessageOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequestMapping("/ws")
public class WebSocketService implements WebSocketHandler {

    private Integer teamId;


    private static final List<WebSocketSession> sessionMap = new ArrayList<>();

    public void sendMessage(Object object, String bo) {

        MessageOutput messageOutput = new MessageOutput(bo, object);
        TextMessage textMessage = new TextMessage(JSONObject.toJSONString(messageOutput));
        broadcastTeamMessage(textMessage);
    }


    public void broadcastTeamMessage(TextMessage message) {
        for (WebSocketSession session : sessionMap) {
            System.out.println("session=" + session);
            if (session.isOpen() && session.getAttributes().get("teamId").equals("1")) {
                // 发送消息
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        teamId = Integer.parseInt(webSocketSession.getAttributes().get("teamId").toString());
        sessionMap.add(webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        System.out.println("hanlerMessage");
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
