package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.BuildLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BuildLogMapper {

    Integer createBuildLog (BuildLog buildLog);

    Integer deleteBuildLogById(Integer id);

    Integer updateBuildLog(BuildLog buildLog);

    BuildLog getBuildLogById(Integer id);

    List<BuildLog> getBuildLogs();
}
