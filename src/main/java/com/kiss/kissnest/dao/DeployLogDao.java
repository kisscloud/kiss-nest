package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.DeployLog;

import java.util.List;

public interface DeployLogDao {

    Integer createDeployLog(DeployLog deployLog);

    Integer deleteDeployLogById(Integer id);

    Integer updateDeployLog(DeployLog deployLog);

    DeployLog getDeployLogById(Integer id);

    List<DeployLog> getDeployLogs();
}
