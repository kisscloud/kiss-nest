package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.DeployLog;
import com.kiss.kissnest.output.DeployLogOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeployLogMapper {

    Integer createDeployLog(DeployLog deployLog);

    DeployLog getDeployLogById(Integer id);

    List<DeployLog> getDeployLogs();

    DeployLogOutput getDeployLogOutputById(Integer id);

    List<DeployLogOutput> getDeployLogsOutputByTeamId(@Param("teamId") Integer teamId,@Param("start") Integer start,@Param("size") Integer size);

    Integer getDeployLogsCount(Integer teamId);

    String getDeployLogOutputTextById(Integer id);
}
