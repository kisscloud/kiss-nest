package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.OperateLogDao;
import com.kiss.kissnest.entity.OperateLog;
import com.kiss.kissnest.mapper.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperateLogDaoImpl implements OperateLogDao {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public Integer createOperateLog(OperateLog operateLog) {

        return operateLogMapper.createOperateLog(operateLog);
    }

    @Override
    public List<OperateLog> getOperateLogsByTeamId(Integer teamId) {

        return operateLogMapper.getOperateLogsByTeamId(teamId);
    }
}
