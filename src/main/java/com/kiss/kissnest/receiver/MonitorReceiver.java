package com.kiss.kissnest.receiver;


import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.dao.ServerMonitorLogDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.entity.ServerMonitorLog;
import com.kiss.kissnest.input.MonitorServerInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RabbitListener(queues = "monitor")
@Slf4j
public class MonitorReceiver {

    @Autowired
    private EnvironmentDao environmentDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private ServerMonitorLogDao serverMonitorLogDao;

    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    public void handler(Object input) {
        Message message = (Message) input;
        String body = new String(message.getBody());
        MonitorServerInput monitorServerInput = JSONObject.parseObject(body, MonitorServerInput.class);
//        environmentDao.getEnvironmentById(1);
//        Environment environment = environmentDao.getEnvironmentByPath(monitorServerInput.getEnvPath());
//        Server server = serverDao.getServerByEnvIdAndInnerIp(environment.getId(), monitorServerInput.getInnerIp());
//        ServerMonitorLog serverMonitorLog = new ServerMonitorLog();
//        serverMonitorLog.setEnvId(environment.getId());
//        serverMonitorLog.setServerId(server.getId());
//        serverMonitorLog.setServerInfo(body);
//        serverMonitorLog.setTeamId(environment.getTeamId());
//        serverMonitorLogDao.createServerMonitorLog(serverMonitorLog);
    }
}
