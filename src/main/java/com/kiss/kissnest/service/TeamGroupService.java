package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.dao.TeamGroupDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.entity.TeamGroup;
import com.kiss.kissnest.output.TeamGroupOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import output.ResultOutput;
import utils.BeanCopyUtil;

@Service
public class TeamGroupService {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private TeamGroupDao teamGroupDao;

    @Transactional
    public ResultOutput createTeamGroup(TeamGroup teamGroup) {

        Team team = teamDao.getTeamById(teamGroup.getId());

        if (team == null) {
            return ResultOutputUtil.error(NestStatusCode.TEAM_NOT_EXIST);
        }

        Group group = groupDao.getGroupById(teamGroup.getGroupId());

        if (group == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_NOT_EXIST);
        }

        Integer count = teamGroupDao.createTeamGroup(teamGroup);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_TEAMGROUP_FAILED);
        }

        teamDao.addGroupsCount(teamGroup.getTeamId());

        return ResultOutputUtil.success(BeanCopyUtil.copy(teamGroup,TeamGroupOutput.class));
    }
}
