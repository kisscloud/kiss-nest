package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.BuildLog;
import org.apache.ibatis.annotations.Mapper;

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

    List<BuildLog> getBuildLogsByTeamId(Map params);
}
