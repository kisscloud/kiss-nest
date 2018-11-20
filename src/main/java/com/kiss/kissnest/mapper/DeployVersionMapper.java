package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.DeployVersion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeployVersionMapper {

    Integer createDeployVersion(DeployVersion deployVersion);

    Integer updateDeployVersion(DeployVersion deployVersion);

    List<DeployVersion> getDeployVersions(DeployVersion deployVersion);
}
