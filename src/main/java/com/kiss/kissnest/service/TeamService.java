package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.MemberTeamDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.dao.TeamGroupDao;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.CreateTeamInput;
import com.kiss.kissnest.input.UpdateTeamInput;
import com.kiss.kissnest.output.TeamOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.foundation.entity.Guest;
import com.kiss.foundation.exception.StatusException;
import org.gitlab.api.models.GitlabGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiss.foundation.utils.BeanCopyUtil;
import com.kiss.foundation.utils.ThreadLocalUtil;

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
    private MemberTeamDao memberTeamDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Autowired
    private OperationLogService operationLogService;

    @Transactional
    public TeamOutput createTeam(CreateTeamInput teamInput) {

        Team team = BeanCopyUtil.copy(teamInput, Team.class);
        Guest guest = ThreadLocalUtil.getGuest();
        team.setOperatorId(guest.getId());
        team.setOperatorName(guest.getName());
        team.setGroupsCount(0);
        team.setMembersCount(1);
        team.setProjectsCount(0);
        Integer count = teamDao.createTeam(team);

        if (count == 0) {
            throw new StatusException(NestStatusCode.CREATE_TEAM_FAILED);
        }

        Integer bindCount = bindMemberTeam(team.getId());

        if (bindCount == 0) {
            throw new TransactionalException(NestStatusCode.BIND_ACCOUNT_TEAM_FAILED);
        }

        Member member = memberDao.getMemberByAccountId(guest.getId());
        MemberTeam memberTeam = generateMemberTeam(member.getId(), team.getId());
        Integer memberCount = memberTeamDao.createMemberTeam(memberTeam);

        if (memberCount == 0) {
            throw new TransactionalException(NestStatusCode.CREATE_MEMBER_TEAM_FAILED);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(guest.getId());
        GitlabGroup gitlabGroup = gitlabApiUtil.createGroup(team.getSlug(), accessToken);

        if (gitlabGroup == null) {
            throw new TransactionalException(NestStatusCode.CREATE_TEAM_REPOSITORY_FAILED);
        }

        team.setRepositoryId(gitlabGroup.getId());
        teamDao.addRepositoryIdById(team);
        memberDao.addCount(member.getId(), 1, "teams");

        Integer id = team.getId();
        team = teamDao.getTeamById(id);
        operationLogService.saveOperationLog(team.getId(), guest, null, team, "id", OperationTargetType.TYPE_CREATE_TEAM);
        operationLogService.saveDynamic(guest, team.getId(), null, null, OperationTargetType.TYPE_CREATE_TEAM, team);
        TeamOutput teamOutput = BeanCopyUtil.copy(team, TeamOutput.class, BeanCopyUtil.defaultFieldNames);

        return teamOutput;
    }

    public void deleteTeamById(Integer id) {

        List<TeamGroup> teamGroupList = teamGroupDao.getTeamGroupsByTeamId(id);

        if (teamGroupList != null && teamGroupList.size() != 0) {
            throw new StatusException(NestStatusCode.TEAM_GROUPS_EXIST);
        }

        Team exist = teamDao.getTeamById(id);

        if (exist == null) {
            throw new StatusException(NestStatusCode.TEAM_NOT_EXIST);
        }

        teamDao.deleteTeamById(id);
    }

    public TeamOutput updateTeam(UpdateTeamInput updateTeamInput) {

        Team team = BeanCopyUtil.copy(updateTeamInput, Team.class);

        Team exist = teamDao.getTeamById(team.getId());

        if (exist == null) {
            throw new StatusException(NestStatusCode.TEAM_NOT_EXIST);
        }

        Integer count = teamDao.updateTeam(team);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_TEAM_FAILED);
        }

        TeamOutput teamOutput = BeanCopyUtil.copy(team, TeamOutput.class, BeanCopyUtil.defaultFieldNames);

        return teamOutput;
    }

    public TeamOutput getTeamById(Integer id) {

        Team team = teamDao.getTeamById(id);

        return BeanCopyUtil.copy(team, TeamOutput.class, BeanCopyUtil.defaultFieldNames);
    }

    public void changeTeam(Integer teamId) {

        Integer count = bindMemberTeam(teamId);

        if (count == 0) {
            throw new StatusException(NestStatusCode.BIND_ACCOUNT_TEAM_FAILED);
        }

    }

    public Integer bindMemberTeam(Integer teamId) {
        //获取当前操作用户
        Guest guest = ThreadLocalUtil.getGuest();
        Integer accountId = guest.getId();
        Member member = memberDao.getMemberByAccountId(accountId);
        Member before = BeanCopyUtil.copy(member, Member.class);

        if (member != null) {
            //更新
            member.setTeamId(teamId);
            Integer count = memberDao.updateMember(member);
            operationLogService.saveOperationLog(teamId, guest, before, member, "id", OperationTargetType.TYPE_CHANGE_TEAM);
            return count;
        }

        member = new Member();
        member.setTeamId(teamId);
        member.setAccountId(accountId);
        member.setName(guest.getName());
        member.setUsername(guest.getName());
        member.setOperatorId(accountId);
        member.setOperatorName(guest.getName());
        Integer count = memberDao.createMember(member);
//        operationLogService.saveOperationLog(teamId,guest,null,member,"id",OperationTargetType.TYPE_CHANGE_TEAM);
        return count;
    }

    public MemberTeam generateMemberTeam(Integer memberId, Integer teamId) {
        MemberTeam memberTeam = new MemberTeam();
        Guest guest = ThreadLocalUtil.getGuest();
        memberTeam.setMemberId(memberId);
        memberTeam.setTeamId(teamId);
        memberTeam.setOperatorId(guest.getId());
        memberTeam.setOperatorName(guest.getName());
        memberTeam.setRole(1);

        return memberTeam;
    }
}
