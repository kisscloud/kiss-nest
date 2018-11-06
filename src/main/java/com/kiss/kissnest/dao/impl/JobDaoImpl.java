package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
