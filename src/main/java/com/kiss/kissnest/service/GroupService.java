package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.GroupProjectDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.GroupProject;
import com.kiss.kissnest.input.CreateGroupInput;
import com.kiss.kissnest.output.GroupOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import output.ResultOutput;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private GroupProjectDao groupProjectDao;

    @Autowired
    private TeamDao teamDao;

    @Transactional
    public ResultOutput createGroup(CreateGroupInput createGroupInput) {

        Group group = (Group) BeanCopyUtil.copy(createGroupInput,Group.class);

        List<Group> groups = groupDao.getGroupsByName(group.getName());

        if (groups != null && groups.size() != 0) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_EXIST);
        }

        Integer count = groupDao.createGroup(group);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_GROUP_FAILED);
        }

        teamDao.addGroupsCount(group.getTeamId());

        return ResultOutputUtil.success(BeanCopyUtil.copy(group,GroupOutput.class));
    }

    public ResultOutput deleteGroup(Integer id) {

        List<GroupProject> groupProjects = groupProjectDao.getGroupProjectsByGroupId(id);

        if (groupProjects != null && groupProjects.size() != 0) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_PROJECT_EXIST);
        }

        Group group = groupDao.getGroupById(id);

        if (group == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
        }

        Integer count = groupDao.deleteGroupById(id);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.DELETE_GROUP_FAILED);
        }

        return ResultOutputUtil.success();
    }

    public ResultOutput updateGroup(Group group) {

        Group exist = groupDao.getGroupById(group.getId());

        if (exist == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
        }

        Integer count = groupDao.updateGroupById(group);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_GROUP_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(group,GroupOutput.class));
    }

    public ResultOutput getGroupById(Integer id) {

        Group group = groupDao.getGroupById(id);

        if (group == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(group,GroupOutput.class));
    }

    public ResultOutput getGroups() {

        List<Group> groups = groupDao.getGroups();

        return ResultOutputUtil.success(BeanCopyUtil.copyList(groups,GroupOutput.class));
    }
}
