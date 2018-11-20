package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.DeployVersionDao;
import com.kiss.kissnest.entity.DeployVersion;
import com.kiss.kissnest.mapper.DeployVersionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeployVersionDaoImpl implements DeployVersionDao {

    @Autowired
    private DeployVersionMapper deployVersionMapper;

    @Override
    public Integer createDeployVersion(DeployVersion deployVersion) {

        return deployVersionMapper.createDeployVersion(deployVersion);
    }

    @Override
    public Integer updateDeployVersion(DeployVersion deployVersion) {

        return deployVersionMapper.updateDeployVersion(deployVersion);
    }

    @Override
    public List<DeployVersion> getDeployVersions(DeployVersion deployVersion) {

        return deployVersionMapper.getDeployVersions(deployVersion);
    }
}
