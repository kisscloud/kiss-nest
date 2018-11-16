package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.OperationLog;

import java.util.List;

public interface OperateLogDao {

    Integer createOperateLog(OperationLog operateLog);

    List<OperationLog> getOperateLogsByTeamId(Integer teamId);
}
