package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.OperationLogDao;
import com.kiss.kissnest.entity.OperationLog;
import com.kiss.kissnest.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OperateLogDaoImpl implements OperationLogDao {

    @Autowired
    private OperationLogMapper operateLogMapper;

    @Override
    public Integer createOperateLog(OperationLog operateLog) {

        return operateLogMapper.createOperateLog(operateLog);
    }

    @Override
    public List<OperationLog> getOperateLogsByTeamId(Integer teamId) {

        return operateLogMapper.getOperateLogsByTeamId(teamId);
    }
}
