package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.BuildLogDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.enums.BuildJobStatusEnums;
import com.kiss.kissnest.mapper.BuildLogMapper;
import com.kiss.kissnest.output.BuildLogOutput;
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

        Map<String, Object> params = new HashMap<>();
        params.put("jobName", jobName);
        params.put("projectId", projectId);

        return buildLogMapper.getLastBuildByJobNameAndProjectId(params);
    }

    @Override
    public List<BuildLogOutput> getBuildLogOutputsByTeamId(Integer teamId, Integer start, Integer size) {

        return buildLogMapper.getBuildLogOutputsByTeamId(teamId, start, size);
    }

    @Override
    public BuildLogOutput getBuildRecentLog(Integer id) {

        return buildLogMapper.getBuildRecentLog(id);
    }

    @Override
    public Integer deleteBuildLogsByProjectId(Integer projectId) {

        return buildLogMapper.deleteBuildLogsByProjectId(projectId);
    }

    @Override
    public Integer getBuildLogCountByTeamId(Integer teamId) {

        return buildLogMapper.getBuildLogCountByTeamId(teamId);
    }

    @Override
    public String getDeployLogOutputTextById(Integer id) {

        return buildLogMapper.getDeployLogOutputTextById(id);
    }

    @Override
    public Integer getPendingBuildLogCount() {

        return buildLogMapper.getBuildLogCountByStatus(BuildJobStatusEnums.PENDING.value());
    }
}
