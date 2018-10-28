package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.DeployLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeployLogMapper {

    Integer createDeployLog(DeployLog deployLog);

    Integer deleteDeployLogById(Integer id);

    Integer updateDeployLog(DeployLog deployLog);

    DeployLog getDeployLogById(Integer id);

    List<DeployLog> getDeployLogs();
}
