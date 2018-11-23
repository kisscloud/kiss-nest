package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.mapper.JobMapper;
import com.kiss.kissnest.output.JobOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobDaoImpl implements JobDao {

    @Autowired
    private JobMapper jobMapper;

    @Override
    public Integer createJob(Job job) {

        return jobMapper.createJob(job);
    }

    @Override
    public List<Job> getJobByProjectId(Integer projectId) {

        return jobMapper.getJobByProjectId(projectId);
    }

    @Override
    public Job getJobByProjectIdAndType(Integer projectId, Integer type) {

        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("type", type);

        return jobMapper.getJobByProjectIdAndType(params);
    }

    @Override
    public Integer updateJobStatus(Integer projectId, Integer type, Integer status,Integer newStatus) {

        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("type", type);
        params.put("status", status);
        params.put("newStatus",newStatus);

        return jobMapper.updateJobStatus(params);
    }

    @Override
    public Integer updateJobStatusAndNumber(Integer projectId, Integer type, Integer status, Integer newStatus, Integer number) {

        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("type", type);
        params.put("status", status);
        params.put("newStatus",newStatus);
        params.put("number",number);

        return jobMapper.updateJobStatusAndNumber(params);
    }

    @Override
    public List<Job> getJobsByTeamId(Integer teamId, Integer type) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("type",type);

        return jobMapper.getJobsByTeamId(params);
    }

    @Override
    public Integer updateBuildJob(Job job) {

        return jobMapper.updateBuildJob(job);
    }

    @Override
    public Job getJobById(Integer id) {

        return jobMapper.getJobById(id);
    }

    @Override
    public Integer deleteJobById(Integer id) {

        return jobMapper.deleteJobById(id);
    }

    @Override
    public List<Job> getJobsByServerIds(Integer serverId) {

        String idStr = "%" + serverId + "%";

        return jobMapper.getJobsByServerIds(idStr);
    }

    @Override
    public Integer updateDeployJob(Job job) {

        return jobMapper.updateDeployJob(job);
    }

    @Override
    public List<JobOutput> getJobOutputsByTeamId(Integer teamId, Integer type) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("type",type);

        return jobMapper.getJobOutputsByTeamId(params);
    }

    @Override
    public JobOutput getJobOutputsById(Integer id) {

        return jobMapper.getJobOutputsById(id);
    }
}
