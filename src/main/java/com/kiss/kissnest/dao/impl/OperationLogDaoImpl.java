package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.OperationLogDao;
import com.kiss.kissnest.entity.OperationLog;
import com.kiss.kissnest.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperationLogDaoImpl implements OperationLogDao {

    @Autowired
    private OperationLogMapper operateLogMapper;

    @Override
    public Integer createOperationLog(OperationLog operateLog) {

        return operateLogMapper.createOperationLog(operateLog);
    }

    @Override
    public List<OperationLog> getOperationLogsByTeamId(Integer teamId) {

        return operateLogMapper.getOperationLogsByTeamId(teamId);
    }
}
