package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.BuildLogDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.mapper.BuildLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BuildLogDaoImpl implements BuildLogDao {

    @Autowired
    private BuildLogMapper buildLogMapper;

    @Override
    public Integer createBuildLog(BuildLog buildLog) {

        return buildLogMapper.createBuildLog(buildLog);
    }

    @Override
    public Integer deleteBuildLogById(Integer id) {

        return buildLogMapper.deleteBuildLogById(id);
    }

    @Override
    public Integer updateBuildLog(BuildLog buildLog) {

        return buildLogMapper.updateBuildLog(buildLog);
    }

    @Override
    public BuildLog getBuildLogById(Integer id) {

        return buildLogMapper.getBuildLogById(id);
    }

    @Override
    public List<BuildLog> getBuildLogs() {

        return buildLogMapper.getBuildLogs();
    }

    @Override
    public BuildLog getLastBuildByJobNameAndProjectId(String jobName, Integer projectId) {

        Map<String,Object> params = new HashMap<>();
        params.put("jobName",jobName);
        params.put("projectId",projectId);

        return buildLogMapper.getLastBuildByJobNameAndProjectId(params);
    }
}
