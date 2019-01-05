package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.GroupProjectDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.GroupProject;
import com.kiss.kissnest.input.BindGroupProjectsInput;
import com.kiss.kissnest.output.BindGroupProjectsOutput;
import com.kiss.kissnest.output.GroupProjectOutput;
import com.kiss.kissnest.status.NestStatusCode;
import exception.StatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import utils.BeanCopyUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupProjectService {

    @Autowired
    private GroupProjectDao groupProjectDao;

    @Autowired
    private GroupDao groupDao;


    public GroupProjectOutput createGroupProject(GroupProject groupProject) {

        Group group = groupDao.getGroupById(groupProject.getGroupId());

        if (group == null) {
            throw new StatusException(NestStatusCode.GROUP_NOT_EXIST);
        }

        Integer count = groupProjectDao.createGroupProject(groupProject);

        if (count == 0) {
            throw new StatusException(NestStatusCode.CREATE_GROUP_PROJECT_FAILED);
        }

        return BeanCopyUtil.copy(groupProject, GroupProjectOutput.class);
    }

    @Transactional
    public List<BindGroupProjectsOutput> bindGroupProjects(BindGroupProjectsInput bindGroupProjectsInput) {

        Integer groupId = bindGroupProjectsInput.getGroupId();

        Group group = groupDao.getGroupById(groupId);

        if (group == null) {
            throw new StatusException(NestStatusCode.GROUP_NOT_EXIST);
        }

        groupProjectDao.deleteGroupProjectByGroupId(groupId);

        List<Integer> projectIds = bindGroupProjectsInput.getProjectIds();

        List<GroupProject> groupProjects = new ArrayList<>();

        for (Integer projectId : projectIds) {
            GroupProject groupProject = new GroupProject();
            groupProject.setGroupId(groupId);
            groupProject.setProjectId(projectId);
            groupProjects.add(groupProject);
        }

        Integer count = groupProjectDao.createGroupProjects(groupProjects);

        if (count == 0) {
            throw new StatusException(NestStatusCode.GROUP_NOT_EXIST);
        }

        groupDao.addCount(groupId, "projects", count);

        return BeanCopyUtil.copyList(groupProjects, BindGroupProjectsOutput.class);
    }
}
