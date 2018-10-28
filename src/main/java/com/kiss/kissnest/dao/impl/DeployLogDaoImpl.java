package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.DeployLogDao;
import com.kiss.kissnest.entity.DeployLog;
import com.kiss.kissnest.mapper.DeployLogMapper;
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
    public Integer deleteDeployLogById(Integer id) {

        return deployLogMapper.deleteDeployLogById(id);
    }

    @Override
    public Integer updateDeployLog(DeployLog deployLog) {

        return deployLogMapper.updateDeployLog(deployLog);
    }

    @Override
    public DeployLog getDeployLogById(Integer id) {

        return deployLogMapper.getDeployLogById(id);
    }

    @Override
    public List<DeployLog> getDeployLogs() {

        return deployLogMapper.getDeployLogs();
    }
}
