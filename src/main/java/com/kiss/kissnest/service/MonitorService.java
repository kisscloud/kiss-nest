package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.kiss.foundation.utils.CryptUtil;
import com.kiss.kissnest.input.MonitorServerInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MonitorService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void cacheServerMonitorData(MonitorServerInput monitorServerInput) {
        String key = CryptUtil.md5(monitorServerInput.getEnvPath() + monitorServerInput.getInnerIp());
        redisTemplate.opsForValue().set(key, JSON.toJSONString(monitorServerInput));
    }

}
