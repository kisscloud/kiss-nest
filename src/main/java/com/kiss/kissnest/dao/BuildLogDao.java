package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.BuildLog;

import java.util.List;

public interface BuildLogDao {

    Integer createBuildLog (BuildLog buildLog);

    Integer deleteBuildLogById(Integer id);

    Integer updateBuildLog(BuildLog buildLog);

    BuildLog getBuildLogById(Integer id);

    List<BuildLog> getBuildLogs();

    BuildLog getLastBuildByJobNameAndProjectId(String jobName,Integer projectId);

    List<BuildLog> getBuildLogsByTeamId(Integer teamId,Integer start,Integer size);
}
