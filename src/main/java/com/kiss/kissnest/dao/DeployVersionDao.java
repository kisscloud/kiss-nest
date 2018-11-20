package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.DeployVersion;

import java.util.List;

public interface DeployVersionDao {

    Integer createDeployVersion(DeployVersion deployVersion);

    Integer updateDeployVersion(DeployVersion deployVersion);

    List<DeployVersion> getDeployVersions(DeployVersion deployVersion);
}
