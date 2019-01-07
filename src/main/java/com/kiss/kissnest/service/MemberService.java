package com.kiss.kissnest.service;

import com.kiss.account.input.ClientAccountInput;
import com.kiss.account.output.AccountOutput;
import com.kiss.account.output.ClientAccountOutput;
import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.RepositoryType;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.feign.AccountServiceFeign;
import com.kiss.kissnest.feign.ClientServiceFeign;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.MemberOutput;
import com.kiss.kissnest.output.MemberRoleOutput;
import com.kiss.kissnest.output.TeamOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.LangUtil;
import kiss.foundation.entity.Guest;
import kiss.foundation.exception.StatusException;
import kiss.foundation.utils.BeanCopyUtil;
import kiss.foundation.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private MemberTeamDao memberTeamDao;

    @Autowired
    private MemberGroupDao memberGroupDao;

    @Autowired
    private MemberProjectDao memberProjectDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @Autowired
    private ClientServiceFeign clientServiceFeign;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private LangUtil langUtil;

    @Value("${member.roles}")
    private String memberRoles;

    @Value("${group.roles}")
    private String groupRoles;

    @Value("${project.roles}")
    private String projectRoles;

    @Autowired
    private AccountServiceFeign accountServiceFeign;

    public MemberOutput createMember(Member member) {

        Member exist = memberDao.getMemberByAccountId(member.getAccountId());

        if (exist != null) {
            throw new StatusException(NestStatusCode.MEMBER_NAME_EXIST);
        }

        Integer count = memberDao.createMember(member);

        if (count == 0) {
            throw new StatusException(NestStatusCode.CREATE_MEMBER_FAILED);
        }

        return BeanCopyUtil.copy(member, MemberOutput.class);
    }

    public void deleteMember(Integer id) {

        Member member = memberDao.getMemberById(id);

        if (member == null) {
            throw new StatusException(NestStatusCode.MEMBER_NOT_EXIST);
        }

        Integer count = memberDao.deleteMemberById(id);

        if (count == 0) {
            throw new StatusException(NestStatusCode.DELETE_MEMBER_FAILED);
        }
    }

    public MemberOutput updateMember(Member member) {

        Member exist = memberDao.getMemberById(member.getId());

        if (exist == null) {
            throw new StatusException(NestStatusCode.MEMBER_NOT_EXIST);
        }

        Integer count = memberDao.updateMember(member);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_MEMBER_FAILED);
        }

        return BeanCopyUtil.copy(member, MemberOutput.class);
    }

    public MemberOutput getMemberById(Integer id) {

        Member member = memberDao.getMemberById(id);

        return BeanCopyUtil.copy(member, MemberOutput.class);
    }

    public MemberOutput getMember() {

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        return BeanCopyUtil.copy(member, MemberOutput.class);
    }

    public Map<String, Object> validateMember() {

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());
        Map<String, Object> result = new HashMap<>();

        if (member == null || StringUtils.isEmpty(member.getApiToken()) || StringUtils.isEmpty(member.getAccessToken())) {
            result.put("validate", false);
        } else {
            result.put("validate", true);
        }

        return result;
    }

    public void getMemberAccess(CreateMemberAccessInput createMemberAccessInput) {

        try {
            Guest guest = ThreadLocalUtil.getGuest();
            String accessToken = gitlabApiUtil.getAccessToken(guest.getUsername(), createMemberAccessInput.getPassword());

            if (StringUtils.isEmpty(accessToken)) {
                throw new StatusException(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);
            }

            Member member = memberDao.getMemberByAccountId(guest.getId());

            if (member != null) {
                Integer count = memberDao.updateAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId(), accessToken);

                if (count == 0) {
                    throw new StatusException(NestStatusCode.UPDATE_MEMBER_ACCESS_FAILED);
                }

            } else {
                member = new Member();
                member.setAccountId(guest.getId());
                member.setName(guest.getName());
                member.setUsername(guest.getName());
                member.setOperatorId(guest.getId());
                member.setOperatorName(guest.getName());
                member.setAccessToken(accessToken);
                Integer count = memberDao.createMember(member);

                if (count == 0) {
                    throw new StatusException(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new StatusException(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);
        }
    }

    public void getMemberApiToken(CreateMemberAccessInput createMemberAccessInput) {

        try {
            Guest guest = ThreadLocalUtil.getGuest();
            String apiToken = jenkinsUtil.generateApiToken(guest.getUsername(), createMemberAccessInput.getPassword());

            if (apiToken == null) {
                throw new StatusException(NestStatusCode.CREATE_MEMBER_APITOKEN_FAILED);
            }

            Member member = memberDao.getMemberByAccountId(guest.getId());

            if (member != null) {
                Integer count = memberDao.updateApiTokenByAccountId(ThreadLocalUtil.getGuest().getId(), apiToken);

                if (count == 0) {
                    throw new StatusException(NestStatusCode.UPDATE_MEMBER_APITOKEN_FAILED);
                }

            } else {
                member = new Member();
                member.setAccountId(guest.getId());
                member.setName(guest.getName());
                member.setUsername(guest.getName());
                member.setOperatorId(guest.getId());
                member.setOperatorName(guest.getName());
                member.setApiToken(apiToken);
                Integer count = memberDao.createMember(member);

                if (count == 0) {
                    throw new StatusException(NestStatusCode.CREATE_MEMBER_APITOKEN_FAILED);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new StatusException(NestStatusCode.CREATE_MEMBER_ACCESS_FAILED);

        }
    }

    public Map<String, Object> getMemberDefaultTeamId(Integer accountId) {

        Team team = memberDao.getMemberDefaultTeamId(accountId);
        Map<String, Object> params = new HashMap<>();
        params.put("teamId", team.getId());
        params.put("teamName", team.getName());

        return params;
    }

    public List<TeamOutput> getMemberTeamsByAccountId(Integer accountId) {

        List<Team> memberTeams = memberTeamDao.getMemberTeams(accountId);
        List<TeamOutput> memberTeamOutputs = BeanCopyUtil.copyList(memberTeams, TeamOutput.class, BeanCopyUtil.defaultFieldNames);

        return memberTeamOutputs;
    }

    public List<ClientAccountOutput> getMembersByClientId(MemberClientInput memberClientInput) {

        ClientAccountInput clientAccountInput = BeanCopyUtil.copy(memberClientInput, ClientAccountInput.class);

        return clientServiceFeign.getClientAccounts(clientAccountInput);
    }

    @Transactional
    public List<MemberOutput> createMemberTeam(CreateMemberTeamInput createMemberTeamInput) {

        Guest guest = ThreadLocalUtil.getGuest();
        List<Member> members = new ArrayList<>();
        List<MemberTeam> memberTeams = new ArrayList<>();
        List<MemberTeamInput> memberInputs = createMemberTeamInput.getMemberTeamInputs();
        Map<Integer, Integer> memberAccount = new HashMap<>();
        Map<String, Integer> gitlabMember = new HashMap<>();
        Map<String, String> memberName = new HashMap<>();
        Map<Integer, AccountOutput> accountOutputMap = new HashMap<>();
        List<MemberOutput> memberOutputs = new ArrayList<>();

        for (MemberTeamInput memberInput : memberInputs) {

            Member member = memberDao.getMemberByAccountId(memberInput.getId());
            MemberOutput memberOutput;

            if (member == null) {
                member = new Member();
                member.setAccountId(memberInput.getId());

                AccountOutput accountOutput = accountServiceFeign.getAccountById(memberInput.getId());
                member.setName(accountOutput.getName());
                accountOutputMap.put(memberInput.getId(), accountOutput);


                member.setTeamId(createMemberTeamInput.getTeamId());
                member.setOperatorId(guest.getId());
                member.setOperatorName(guest.getName());
                members.add(member);
                memberOutput = new MemberOutput();
                memberOutput.setName(member.getName());
                memberOutput.setRoleId(memberInput.getRole());
                memberOutput.setGroupsCount(0);
                memberOutput.setProjectsCount(0);
            } else {
                memberAccount.put(member.getAccountId(), member.getId());
                memberOutput = BeanCopyUtil.copy(member, MemberOutput.class, BeanCopyUtil.defaultFieldNames);
            }

            memberOutput.setRoleText(langUtil.getEnumsMessage("member.role", String.valueOf(memberInput.getRole())));
            memberOutputs.add(memberOutput);
        }

        if (members.size() != 0) {
            Integer count = memberDao.createMembers(members);

            if (count != members.size()) {
                throw new TransactionalException(NestStatusCode.CREATE_MEMBER_FAILED);
            }

            for (Member member : members) {
                memberAccount.put(member.getAccountId(), member.getId());
            }
        }

        for (MemberTeamInput memberInput : memberInputs) {
            Integer memberId = memberAccount.get(memberInput.getId());
            MemberTeam memberTeam = memberTeamDao.getMemberTeam(createMemberTeamInput.getTeamId(), memberId);

            if (memberTeam == null) {
                memberTeam = new MemberTeam();
                memberTeam.setMemberId(memberId);
                memberTeam.setTeamId(createMemberTeamInput.getTeamId());
                memberTeam.setRole(memberInput.getRole());
                memberTeam.setOperatorId(guest.getId());
                memberTeam.setOperatorName(guest.getName());
                memberTeams.add(memberTeam);
                AccountOutput accountOutput = accountOutputMap.get(memberInput.getId());

                if (accountOutput != null) {
                    gitlabMember.put(accountOutput.getUsername(), memberInput.getRole());
                    memberName.put(accountOutput.getUsername(), accountOutput.getName());
                }

            }
        }

        if (memberTeams.size() != 0) {
            Integer count = memberTeamDao.createMemberTeams(memberTeams);

            if (count != memberTeams.size()) {
                throw new TransactionalException(NestStatusCode.CREATE_MEMBER_TEAM_FAILED);
            }
        } else {
            throw new StatusException(NestStatusCode.TEAM_MEMBER_IS_EXIST);
        }

        teamDao.addCount("members", memberTeams.size(), createMemberTeamInput.getTeamId());

        for (MemberTeam memberTeam : memberTeams) {
            memberDao.addCount(memberTeam.getMemberId(), 1, "teams");
        }

        Team team = teamDao.getTeamById(createMemberTeamInput.getTeamId());
        Member operator = memberDao.getMemberByAccountId(guest.getId());

        for (Map.Entry<String, Integer> entry : gitlabMember.entrySet()) {
            gitlabApiUtil.addMember(team.getRepositoryId(), operator.getAccessToken(), entry.getKey(), entry.getValue(), RepositoryType.Group, memberName.get(entry.getKey()));
        }

        return memberOutputs;
    }

    @Transactional
    public void createMemberGroup(BindMemberGroupInput bindMemberGroupInput) {

        Guest guest = ThreadLocalUtil.getGuest();
        List<MemberGroup> memberGroups = new ArrayList<>();
        List<MemberGroupInput> memberGroupInputs = bindMemberGroupInput.getMemberGroupInputs();
        Map<String, Integer> gitlabGroup = new HashMap<>();

        for (MemberGroupInput memberGroupInput : memberGroupInputs) {
            MemberGroup memberGroup = memberGroupDao.getMemberGroup(bindMemberGroupInput.getTeamId(), bindMemberGroupInput.getGroupId(), memberGroupInput.getMemberId());

            if (memberGroup == null) {
                memberGroup = new MemberGroup();
                memberGroup.setMemberId(memberGroupInput.getMemberId());
                memberGroup.setTeamId(bindMemberGroupInput.getTeamId());
                memberGroup.setRole(memberGroupInput.getRole());
                memberGroup.setOperatorId(guest.getId());
                memberGroup.setOperatorName(guest.getName());
                memberGroup.setGroupId(bindMemberGroupInput.getGroupId());
                memberGroups.add(memberGroup);
                Member member = memberDao.getMemberById(memberGroupInput.getMemberId());
                gitlabGroup.put(member.getUsername(), memberGroupInput.getRole());
            }
        }

        if (memberGroups.size() != 0) {

            Integer count = memberGroupDao.createMemberGroups(memberGroups);

            if (count != memberGroups.size()) {
                throw new TransactionalException(NestStatusCode.CREATE_MEMBER_GROUP_FAILED);
            }

            memberGroups.forEach(memberGroup -> memberDao.addCount(memberGroup.getMemberId(), 1, "groups"));

        } else {
            throw new StatusException(NestStatusCode.GROUP_MEMBER_IS_EXIST);
        }

        groupDao.addCount(bindMemberGroupInput.getGroupId(), "members", memberGroups.size());

        for (MemberGroup memberGroup : memberGroups) {
            memberDao.addCount(memberGroup.getMemberId(), 1, "groups");
        }

        Group group = groupDao.getGroupById(bindMemberGroupInput.getGroupId());
        Member operator = memberDao.getMemberByAccountId(guest.getId());

        for (Map.Entry<String, Integer> entry : gitlabGroup.entrySet()) {
            gitlabApiUtil.addMember(group.getRepositoryId(), operator.getAccessToken(), entry.getKey(), entry.getValue(), RepositoryType.SubGroup, null);
        }
    }

    @Transactional
    public void createMemberProject(BindMemberProjectInput bindMemberProjectInput) {

        Guest guest = ThreadLocalUtil.getGuest();
        List<MemberProject> memberProjects = new ArrayList<>();
        List<MemberProjectInput> memberProjectInputs = bindMemberProjectInput.getMemberProjectInputs();
        Map<String, Integer> gitlabProject = new HashMap<>();

        for (MemberProjectInput memberProjectInput : memberProjectInputs) {
            MemberProject memberProject = memberProjectDao.getMemberProject(bindMemberProjectInput.getTeamId(), bindMemberProjectInput.getProjectId(), memberProjectInput.getMemberId());

            if (memberProject == null) {
                memberProject = new MemberProject();
                memberProject.setMemberId(memberProjectInput.getMemberId());
                memberProject.setTeamId(bindMemberProjectInput.getTeamId());
                memberProject.setRole(memberProjectInput.getRole());
                memberProject.setOperatorId(guest.getId());
                memberProject.setOperatorName(guest.getName());
                memberProject.setProjectId(bindMemberProjectInput.getProjectId());
                memberProjects.add(memberProject);
                Member member = memberDao.getMemberById(memberProjectInput.getMemberId());
                gitlabProject.put(member.getUsername(), memberProjectInput.getRole());
            }
        }

        if (memberProjects.size() != 0) {

            Integer count = memberProjectDao.createMemberProjects(memberProjects);

            if (count != memberProjects.size()) {
                throw new TransactionalException(NestStatusCode.CREATE_MEMBER_PROJECT_FAILED);
            }

            memberProjects.forEach(memberProject -> memberDao.addCount(memberProject.getMemberId(), 1, "projects"));
        } else {
            throw new StatusException(NestStatusCode.PROJCET_MEMBER_IS_EXIST);
        }

        projectDao.addCount(bindMemberProjectInput.getProjectId(), "members", memberProjects.size());

        for (MemberProject memberProject : memberProjects) {
            memberDao.addCount(memberProject.getMemberId(), 1, "projects");
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(bindMemberProjectInput.getProjectId());
        Member operator = memberDao.getMemberByAccountId(guest.getId());

        for (Map.Entry<String, Integer> entry : gitlabProject.entrySet()) {
            gitlabApiUtil.addMember(projectRepository.getRepositoryId(), operator.getAccessToken(), entry.getKey(), entry.getValue(), RepositoryType.Project, null);
        }

    }

    public List<MemberRoleOutput> getMemberRoles(Integer type) {

        String[] roles = null;
        String key = null;

        switch (type) {
            case 1:
                roles = memberRoles.split(",");
                key = "member.role";
                break;
            case 2:
                roles = groupRoles.split(",");
                key = "group.role";
                break;
            case 3:
                roles = projectRoles.split(",");
                key = "project.role";
        }

        List<MemberRoleOutput> memberRoleOutputs = new ArrayList<>();

        if (roles != null) {
            for (String role : roles) {
                MemberRoleOutput memberRoleOutput = new MemberRoleOutput();
                memberRoleOutput.setRoleId(Integer.parseInt(role));
                memberRoleOutput.setRoleName(langUtil.getEnumsMessage(key, role));
                memberRoleOutputs.add(memberRoleOutput);
            }
        }

        return memberRoleOutputs;
    }

    public List<MemberOutput> getMembers(Integer teamId, Integer groupId, Integer projectId) {

        List<Member> members = memberDao.getMembers(teamId, groupId, projectId);
        List<MemberOutput> memberOutputs = BeanCopyUtil.copyList(members, MemberOutput.class, BeanCopyUtil.defaultFieldNames);

        return memberOutputs;
    }

    public List<MemberOutput> getMemberTeamsByTeamId(Integer teamId) {
        List<Member> members = memberTeamDao.getMemberTeamsByTeamId(teamId);
        List<MemberOutput> memberOutputs = BeanCopyUtil.copyList(members, MemberOutput.class, BeanCopyUtil.defaultFieldNames);

        return memberOutputs;
    }

    public List<MemberOutput> getGroupValidMembers(GroupMemberSearchInput groupMemberSearchInput) {

        List<Member> members = memberDao.getGroupValidMembers(groupMemberSearchInput.getGroupId(), groupMemberSearchInput.getAccountName());
        List<MemberOutput> memberOutputs = BeanCopyUtil.copyList(members, MemberOutput.class);

        return memberOutputs;
    }

    public List<MemberOutput> getProjectValidMembers(ProjectMemberSearchInput projectMemberSearchInput) {

        List<Member> members = memberDao.getProjectValidMembers(projectMemberSearchInput.getProjectId(), projectMemberSearchInput.getAccountName());
        List<MemberOutput> memberOutputs = BeanCopyUtil.copyList(members, MemberOutput.class);

        return memberOutputs;
    }
}
