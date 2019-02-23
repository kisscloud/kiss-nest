package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.kiss.foundation.utils.CryptUtil;
import com.kiss.kissnest.enums.WebSocketMessageTypeEnums;
import com.kiss.kissnest.input.MonitorServerInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private WebSocketService webSocketService;

    public void cacheServerMonitorData(MonitorServerInput monitorServerInput) {
        String key = CryptUtil.md5(monitorServerInput.getEnvPath() + monitorServerInput.getInnerIp());
        redisTemplate.opsForValue().set(key, JSON.toJSONString(monitorServerInput));
        webSocketService.sendMessage(WebSocketMessageTypeEnums.SERVER_MONITOR_LOG.value(), monitorServerInput);

    }



}
