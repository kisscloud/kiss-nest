package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.DeployVersionDao;
import com.kiss.kissnest.entity.DeployVersion;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.ThreadLocalUtil;

import java.util.List;

@Service
public class DeployVersionService {

    @Autowired
    private DeployVersionDao deployVersionDao;

    public Integer createDeployVersion(Integer projectId,String serverId,String branch,String tag,Integer type,String version) {

        DeployVersion deployVersion = new DeployVersion();
        Guest guest = ThreadLocalUtil.getGuest();
        deployVersion.setProjectId(projectId);
        deployVersion.setServerId(serverId);
        deployVersion.setBranch(branch);
        deployVersion.setTag(tag);
        deployVersion.setVersion(version);
        deployVersion.setOperatorId(guest.getId());
        deployVersion.setOperatorName(guest.getName());

        return deployVersionDao.createDeployVersion(deployVersion);
    }

    public Integer updateDeployVersion(Integer id,String branch,String tag,Integer type,String version) {

        DeployVersion deployVersion = new DeployVersion();
        Guest guest = ThreadLocalUtil.getGuest();
        deployVersion.setBranch(branch);
        deployVersion.setTag(tag);
        deployVersion.setVersion(version);
        deployVersion.setOperatorId(guest.getId());
        deployVersion.setOperatorName(guest.getName());

        return deployVersionDao.updateDeployVersion(deployVersion);
    }

    public List<DeployVersion> getDeployVersions(Integer projectId,String serverId,String branch,String tag,Integer type,String version) {

        DeployVersion deployVersion = new DeployVersion();
        deployVersion.setBranch(branch);
        deployVersion.setTag(tag);
        deployVersion.setVersion(version);
        deployVersion.setProjectId(projectId);
        deployVersion.setServerId(serverId);

        return deployVersionDao.getDeployVersions(deployVersion);
    }

}
