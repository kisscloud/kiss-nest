package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ServerMonitorLogDao;
import com.kiss.kissnest.entity.ServerMonitorLog;
import com.kiss.kissnest.mapper.ServerMonitorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerMonitorLogDaoImpl implements ServerMonitorLogDao {

    @Autowired
    private ServerMonitorLogMapper serverMonitorLogMapper;

    @Override
    public Integer createServerMonitorLog(ServerMonitorLog serverMonitorLog) {
        return serverMonitorLogMapper.createServerMonitorLog(serverMonitorLog);
    }
}
