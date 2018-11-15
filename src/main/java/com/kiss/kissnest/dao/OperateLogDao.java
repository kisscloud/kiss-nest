package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.OperateLog;

import java.util.List;

public interface OperateLogDao {

    Integer createOperateLog(OperateLog operateLog);

    List<OperateLog> getOperateLogsByTeamId(Integer teamId);
}
