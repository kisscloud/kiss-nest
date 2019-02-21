package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.DeployNodeLogDao;
import com.kiss.kissnest.entity.DeployNodeLog;
import com.kiss.kissnest.mapper.DeployNodeLogMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class DeployNodeLogDaoImpl implements DeployNodeLogDao {

    @Autowired
    private DeployNodeLogMapper deployNodeLogMapper;

    @Override
    public Integer createDeployNodeLog(DeployNodeLog deployNodeLog) {
        return deployNodeLogMapper.createDeployNodeLog(deployNodeLog);
    }
}
