package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.DeployLog;
import com.kiss.kissnest.output.DeployLogOutput;

import java.util.List;

public interface DeployLogDao {

    Integer createDeployLog(DeployLog deployLog);

    DeployLog getDeployLogById(Integer id);

    List<DeployLog> getDeployLogs();

    DeployLogOutput getDeployLogOutputById(Integer id);

    List<DeployLogOutput> getDeployLogsOutputByTeamId(Integer teamId, Integer start, Integer size);

    Integer getDeployLogsCount(Integer teamId);

    String getDeployLogOutputTextById(Integer id);
}
