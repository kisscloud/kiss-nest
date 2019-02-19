package com.kiss.kissnest.service;

import com.kiss.kissnest.input.MonitorServerInput;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MonitorService {

    @Resource
    public RedisTemplate redisTemplate;

    public void cacheServerMonitorData(MonitorServerInput monitorServerInput) {

    }

}
