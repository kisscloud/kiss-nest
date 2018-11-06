package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.dao.TeamGroupDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.entity.TeamGroup;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.CreateTeamInput;
import com.kiss.kissnest.output.TeamOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import entity.Guest;
import org.gitlab.api.models.GitlabGroup;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import output.ResultOutput;
import utils.ThreadLocalUtil;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private TeamGroupDao teamGroupDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Transactional
    public ResultOutput createTeam(CreateTeamInput teamInput) {

        Team team = (Team) BeanCopyUtil.copy(teamInput,Team.class);

        Integer count = teamDao.createTeam(team);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_TEAM_FAILED);
        }

        Integer bindCount = bindMemberTeam(team.getId());

        if (bindCount == 0) {
            throw new TransactionalException(NestStatusCode.BIND_ACCOUNT_TEAM_FAILED);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
        GitlabGroup gitlabGroup = gitlabApiUtil.createGroup(team.getSlug(),accessToken);

        if (gitlabGroup == null) {
            throw  new TransactionalException(NestStatusCode.CREATE_TEAM_REPOSITORY_FAILED);
        }

        team.setRepositoryId(gitlabGroup.getId());
        teamDao.addRepositoryIdById(team);

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

    public ResultOutput changeTeam(Integer teamId) {

        Integer count = bindMemberTeam(teamId);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.BIND_ACCOUNT_TEAM_FAILED);
        }

        return ResultOutputUtil.success();
    }

    public Integer bindMemberTeam (Integer teamId) {
        //获取当前操作用户
        Guest guest = ThreadLocalUtil.getGuest();
        Integer accountId = guest.getId();
        Member member = memberDao.getMemberByAccountId(accountId);

        if (member != null) {
            //更新
            member.setTeamId(teamId);
            Integer count = memberDao.updateMember(member);
            return count;
        }

        member = new Member();
        member.setTeamId(teamId);
        member.setAccountId(accountId);
        member.setOperatorId(accountId);
        member.setOperatorName(guest.getName());
        Integer count = memberDao.createMember(member);

        return count;
    }
}
