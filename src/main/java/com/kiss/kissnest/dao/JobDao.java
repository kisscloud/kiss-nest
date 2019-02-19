package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.output.JobOutput;

import java.util.List;

public interface JobDao {

    Integer createJob(Job job);

    List<Job> getJobByProjectId(Integer projectId);

    Job getJobByProjectIdAndType(Integer projectId, Integer type);

    Integer updateJobStatus(Integer projectId, Integer type, Integer status, Integer newStatus);

    Integer updateJobStatusAndNumber(Integer projectId, Integer type, Integer status, Integer newStatus, Integer number);

    List<Job> getJobsByTeamId(Integer teamId, Integer type);

    Integer updateBuildJob(Job job);

    Job getJobById(Integer id);

    Integer deleteJobById(Integer id);

    List<Job> getJobsByServerIds(Integer serverId);

    Integer updateDeployJob(Job job);

    List<JobOutput> getJobOutputsByTeamId(Integer teamId, Integer type);

    JobOutput getJobOutputsById(Integer id);

    Job getDeployJobByProjectIdAndEnvId(Integer projectId, Integer envId);

    Job getBuildJobByProjectId(Integer projectId);
}
