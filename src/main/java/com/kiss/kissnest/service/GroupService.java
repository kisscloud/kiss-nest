package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.CreateGroupInput;
import com.kiss.kissnest.input.UpdateGroupInput;
import com.kiss.kissnest.output.GroupOutput;
import com.kiss.kissnest.output.MemberOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.LangUtil;
import com.kiss.foundation.entity.Guest;
import com.kiss.foundation.exception.StatusException;
import com.kiss.foundation.utils.BeanCopyUtil;
import com.kiss.foundation.utils.ThreadLocalUtil;
import org.gitlab.api.models.GitlabGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private GroupProjectDao groupProjectDao;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Autowired
    private LangUtil langUtil;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private MemberGroupDao memberGroupDao;

    @Transactional
    public GroupOutput createGroup(CreateGroupInput createGroupInput) {

        Group group = BeanCopyUtil.copy(createGroupInput, Group.class);
        Guest guest = ThreadLocalUtil.getGuest();
        group.setStatus(1);
        group.setMembersCount(1);
        group.setProjectsCount(0);
        group.setOperatorId(guest.getId());
        group.setOperatorName(guest.getName());
        Integer count = groupDao.createGroup(group);

        if (count == 0) {
            throw new StatusException(NestStatusCode.CREATE_GROUP_FAILED);
        }

        teamDao.addCount("groups", 1, group.getTeamId());
        Member member = memberDao.getMemberByAccountId(ThreadLocalUtil.getGuest().getId());
        memberDao.addCount(member.getId(), 1, "groups");

        String accessToken = member.getAccessToken();
        Integer parentId = teamDao.getRepositoryIdByTeamId(group.getTeamId());

        if (parentId == null) {
            throw new TransactionalException(NestStatusCode.GROUP_PARENTID_LOSED);
        }

        MemberGroup memberGroup = new MemberGroup();
        memberGroup.setGroupId(group.getId());
        memberGroup.setMemberId(member.getId());
        memberGroup.setOperatorId(guest.getId());
        memberGroup.setOperatorName(guest.getName());
        memberGroup.setRole(1);
        memberGroup.setTeamId(group.getTeamId());
        Integer memberGroupCount = memberGroupDao.createMemberGroup(memberGroup);

        if (memberGroupCount == 0) {
            throw new TransactionalException(NestStatusCode.CREATE_MEMBER_GROUP_FAILED);
        }

        GitlabGroup gitlabGroup = gitlabApiUtil.createSubGroup(group.getSlug(), accessToken, parentId);

        if (gitlabGroup == null) {
            throw new TransactionalException(NestStatusCode.CREATE_GROUP_REPOSITORY_FAILED);
        }

        group.setRepositoryId(gitlabGroup.getId());
        groupDao.addRepositoryIdById(group);
        Integer id = group.getId();
        group = groupDao.getGroupById(id);
        operationLogService.saveOperationLog(group.getTeamId(), guest, null, group, "id", OperationTargetType.TYPE_CREATE_GROUP);
        operationLogService.saveDynamic(guest, group.getTeamId(), group.getId(), null, OperationTargetType.TYPE_CREATE_GROUP, group);
        GroupOutput groupOutput = BeanCopyUtil.copy(group, GroupOutput.class);
        groupOutput.setStatusText(langUtil.getEnumsMessage("group.status", String.valueOf(groupOutput.getStatus())));

        return groupOutput;
    }

    @Transactional
    public void deleteGroup(Integer id) {

        List<Project> projects = projectDao.getProjectsByGroupId(id);

        if (projects != null && projects.size() != 0) {
            throw new StatusException(NestStatusCode.GROUP_PROJECT_EXIST);
        }

        Group group = groupDao.getGroupById(id);

        if (group == null) {
            throw new StatusException(NestStatusCode.GROUP_NOT_EXIST);
        }

        Integer count = groupDao.deleteGroupById(id);

        if (count == 0) {
            throw new StatusException(NestStatusCode.DELETE_GROUP_FAILED);
        }

        List<MemberGroup> memberGroups = memberGroupDao.getMemberGroups(group.getTeamId(),id);

        for (MemberGroup memberGroup : memberGroups) {
            Integer memberCount = memberDao.deleteCount(memberGroup.getMemberId(),1,"groups");

            if (memberCount == 0) {
                throw new TransactionalException(NestStatusCode.DELETE_MEMBER_GROUP_COUNT_FAILED);
            }
        }

        Integer memberGroupCount = memberGroupDao.deleteMemberGroupsByGroupId(id);

        if (memberGroupCount == 0) {
            throw new TransactionalException(NestStatusCode.DELETE_MEMBER_GROUP_FAILED);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
        boolean flag = gitlabApiUtil.deleteGroup(group.getRepositoryId(), accessToken);

        if (!flag) {
            throw new TransactionalException(NestStatusCode.DELETE_GROUP_REPOSITORY_FAILED);
        }

        operationLogService.saveOperationLog(group.getTeamId(), ThreadLocalUtil.getGuest(), group, null, "id", OperationTargetType.TYPE_DELETE_GROUP);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(), group.getTeamId(), group.getId(), null, OperationTargetType.TYPE_DELETE_GROUP, group);

    }

    public GroupOutput updateGroup(UpdateGroupInput updateGroupInput) {

        Group group = BeanCopyUtil.copy(updateGroupInput, Group.class);
        Guest guest = ThreadLocalUtil.getGuest();
        group.setOperatorId(guest.getId());
        group.setOperatorName(guest.getName());
        group.setStatus(1);
        Group oldValue = groupDao.getGroupById(group.getId());
        Integer count = groupDao.updateGroupById(group);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_GROUP_FAILED);
        }

        operationLogService.saveOperationLog(group.getTeamId(), guest, oldValue, group, "id", OperationTargetType.TYPE_UPDATE_GROUP);
        operationLogService.saveDynamic(guest, group.getTeamId(), group.getId(), null, OperationTargetType.TYPE_UPDATE_GROUP, group);

        return BeanCopyUtil.copy(group, GroupOutput.class);
    }

    public GroupOutput getGroupById(Integer id) {

        Group group = groupDao.getGroupById(id);

        if (group == null) {
            throw new StatusException(NestStatusCode.GROUP_NOT_EXIST);
        }

        return BeanCopyUtil.copy(group, GroupOutput.class);
    }

    public List<GroupOutput> getGroups(Integer teamId) {

        List<Group> groups = groupDao.getGroups(teamId);

        List<GroupOutput> groupOutputs = BeanCopyUtil.copyList(groups, GroupOutput.class, BeanCopyUtil.defaultFieldNames);

        groupOutputs.forEach(groupOutput -> groupOutput.setStatusText(langUtil.getEnumsMessage("group.status", String.valueOf(groupOutput.getStatus()))));

        return groupOutputs;
    }

    public List<MemberOutput> getMemberGroupsByGroupId(Integer groupId) {

        List<Member> members = memberGroupDao.getMemberGroupsByGroupId(groupId);
        List<MemberOutput> memberOutputs = BeanCopyUtil.copyList(members,MemberOutput.class,BeanCopyUtil.defaultFieldNames);

        return memberOutputs;
    }
}
