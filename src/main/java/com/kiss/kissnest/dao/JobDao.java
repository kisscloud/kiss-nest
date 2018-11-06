package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Job;

public interface JobDao {

    Integer createJob(Job job);

    Job getJobByProjectId(Integer projectId);
}
