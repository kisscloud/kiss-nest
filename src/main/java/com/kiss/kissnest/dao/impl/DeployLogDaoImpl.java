package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.DeployLogDao;
import com.kiss.kissnest.entity.DeployLog;
import com.kiss.kissnest.mapper.DeployLogMapper;
import com.kiss.kissnest.output.DeployLogOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeployLogDaoImpl implements DeployLogDao {

    @Autowired
    private DeployLogMapper deployLogMapper;

    @Override
    public Integer createDeployLog(DeployLog deployLog) {

        return deployLogMapper.createDeployLog(deployLog);
    }

    @Override
    public DeployLog getDeployLogById(Integer id) {

        return deployLogMapper.getDeployLogById(id);
    }

    @Override
    public List<DeployLog> getDeployLogs() {

        return deployLogMapper.getDeployLogs();
    }

    @Override
    public DeployLogOutput getDeployLogOutputById(Integer id) {

        return deployLogMapper.getDeployLogOutputById(id);
    }

    @Override
    public List<DeployLogOutput> getDeployLogsOutputByTeamId(Integer teamId, Integer start, Integer size) {

        return deployLogMapper.getDeployLogsOutputByTeamId(teamId,start,size);
    }
}
