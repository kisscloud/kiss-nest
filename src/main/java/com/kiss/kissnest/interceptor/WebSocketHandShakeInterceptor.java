package com.kiss.kissnest.interceptor;

import com.kiss.kissnest.feign.ClientServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class WebSocketHandShakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private ClientServiceFeign clientServiceFeign;

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {

        HttpServletRequest servletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
        String teamId = servletRequest.getParameter("teamId");
        String token = servletRequest.getParameter("token");
        Boolean effective = clientServiceFeign.validateClientToken(token);

        if (!effective) {
            return false;
        }

        map.put("teamId", teamId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }


}
