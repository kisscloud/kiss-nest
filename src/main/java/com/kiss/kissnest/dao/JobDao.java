package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Job;

import java.util.List;

public interface JobDao {

    Integer createJob(Job job);

    List<Job> getJobByProjectId(Integer projectId);

    Job getJobByProjectIdAndType(Integer projectId, Integer type);

    Integer updateJobStatus(Integer projectId, Integer type, Integer status,Integer newStatus);

    Integer updateJobStatusAndNumber(Integer projectId, Integer type, Integer status,Integer newStatus,Integer number);

    List<Job> getJobsByTeamId(Integer teamId,Integer type);

    Integer updateJob(Job job);

    Job getJobById(Integer id);

    Integer deleteJobById(Integer id);

    List<Job> getJobsByServerIds(Integer serverId);
}
