package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.DeployNodeLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeployNodeLogMapper {
    Integer createDeployNodeLog(DeployNodeLog deployNodeLog);
}
