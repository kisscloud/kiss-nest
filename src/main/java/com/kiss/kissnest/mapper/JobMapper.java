package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.output.JobOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface JobMapper {

    Integer createJob(Job job);

    List<Job> getJobByProjectId(Integer projectId);

    Integer updateJobStatus(Map params);

    Integer updateJobStatusAndNumber(Map params);

    List<Job> getJobsByTeamId(Map params);

    Integer updateBuildJob(Job job);

    Job getJobById(Integer id);

    Integer deleteJobById(Integer id);

    List<Job> getJobsByServerIds(String serverId);

    Integer updateDeployJob(Job job);

    List<JobOutput> getJobOutputsByTeamId(Map params);

    JobOutput getJobOutputsById(Integer id);

    Job getDeployJobByProjectIdAndEnvId(@Param("projectId") Integer projectId, @Param("envId") Integer envId);

    Job getJobByProjectIdAndType(@Param("projectId") Integer projectId, @Param("type") Integer type);
}
