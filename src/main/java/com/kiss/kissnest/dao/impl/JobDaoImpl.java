package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    public Job getJobByProjectId(Integer projectId) {

        return jobMapper.getJobByProjectId(projectId);
    }

    @Override
    public Job getJobByProjectIdAndType(Integer projectId, Integer type) {

        Map<String,Object> params = new HashMap<>();
        params.put("projectId",projectId);
        params.put("type",type);

        return jobMapper.getJobByProjectIdAndType(params);
    }
}
