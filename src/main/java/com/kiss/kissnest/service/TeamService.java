package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.dao.TeamGroupDao;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.entity.TeamGroup;
import com.kiss.kissnest.output.TeamOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import output.ResultOutput;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private TeamGroupDao teamGroupDao;

    public ResultOutput createTeam(Team team) {

        Integer count = teamDao.createTeam(team);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_TEAM_FAILED);
        }

        TeamOutput teamOutput = new TeamOutput();
        BeanUtils.copyProperties(team,teamOutput);

        return ResultOutputUtil.success(teamOutput);
    }

    public ResultOutput deleteTeamById(Integer id) {

        List<TeamGroup> teamGroupList = teamGroupDao.getTeamGroupsByTeamId(id);

        if (teamGroupList != null && teamGroupList.size() != 0) {
            return ResultOutputUtil.error(NestStatusCode.TEAM_GROUPS_EXIST);
        }

        Team exist = teamDao.getTeamById(id);

        if (exist == null) {
            return ResultOutputUtil.error(NestStatusCode.TEAM_NOT_EXIST);
        }

        teamDao.deleteTeamById(id);

        return ResultOutputUtil.success();
    }

    public ResultOutput updateTeam(Team team) {

        Team exist = teamDao.getTeamById(team.getId());

        if (exist == null) {
            return ResultOutputUtil.error(NestStatusCode.TEAM_NOT_EXIST);
        }

        Integer count = teamDao.updateTeam(team);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_TEAM_FAILED);
        }

        TeamOutput teamOutput = new TeamOutput();
        BeanUtils.copyProperties(team,teamOutput);

        return ResultOutputUtil.success(teamOutput);
    }

    public ResultOutput getTeamById (Integer id) {

        Team team = teamDao.getTeamById(id);

        return ResultOutputUtil.success(BeanCopyUtil.copy(team,TeamOutput.class));
    }

    public ResultOutput getTeams () {

        List<Team> teams = teamDao.getTeams();

        return ResultOutputUtil.success(BeanCopyUtil.copyList(teams,TeamOutput.class));
    }
}
