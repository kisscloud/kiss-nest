package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Job;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface JobMapper {

    Integer createJob(Job job);

    Job getJobByProjectId(Integer projectId);

    Job getJobByProjectIdAndType(Map params);
}
