package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.GroupProjectDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.GroupProject;
import com.kiss.kissnest.input.BindGroupProjectsInput;
import com.kiss.kissnest.output.BindGroupProjectsOutput;
import com.kiss.kissnest.output.GroupProjectOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import output.ResultOutput;
import utils.BeanCopyUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupProjectService {

    @Autowired
    private GroupProjectDao groupProjectDao;

    @Autowired
    private GroupDao groupDao;


    public ResultOutput createGroupProject(GroupProject groupProject) {

        Group group = groupDao.getGroupById(groupProject.getGroupId());

        if (group == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
        }

        Integer count = groupProjectDao.createGroupProject(groupProject);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_GROUP_PROJECT_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(groupProject,GroupProjectOutput.class));
    }

    @Transactional
    public ResultOutput bindGroupProjects(BindGroupProjectsInput bindGroupProjectsInput) {

        Integer groupId = bindGroupProjectsInput.getGroupId();

        Group group = groupDao.getGroupById(groupId);

        if (group == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
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
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
        }

        groupDao.addCount(group.getTeamId(),groupId,"projects",count);

        return ResultOutputUtil.success(BeanCopyUtil.copyList(groupProjects,BindGroupProjectsOutput.class));
    }
}
