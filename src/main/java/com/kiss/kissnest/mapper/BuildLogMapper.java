package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.output.BuildLogOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BuildLogMapper {

    Integer createBuildLog (BuildLog buildLog);

    Integer deleteBuildLogById(Integer id);

    Integer updateBuildLog(BuildLog buildLog);

    BuildLog getBuildLogById(Integer id);

    List<BuildLog> getBuildLogs();

    BuildLog getLastBuildByJobNameAndProjectId(Map params);

    List<BuildLogOutput> getBuildLogOutputsByTeamId(@Param("teamId") Integer teamId,@Param("start") Integer start,@Param("size") Integer size);

    BuildLogOutput getBuildRecentLog(Integer id);

    Integer deleteBuildLogsByProjectId(Integer projectId);

    Integer getBuildLogCountByTeamId(Integer teamId);

    String getDeployLogOutputTextById(Integer id);

    Integer getBuildLogCountByStatus(Integer status);
}
