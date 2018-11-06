package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobMapper {

    Integer createJob(Job job);

    Job getJobByProjectId(Integer projectId);
}
