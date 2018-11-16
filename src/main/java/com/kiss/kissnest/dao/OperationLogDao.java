package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.OperationLog;

import java.util.List;

public interface OperationLogDao {

    Integer createOperationLog(OperationLog operateLog);

    List<OperationLog> getOperationLogsByTeamId(Integer teamId);
}
