package com.kiss.kissnest.validator;

import com.kiss.account.input.ValidateAccountInput;
import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.feign.AccountServiceFeign;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.status.NestStatusCode;
import entity.Guest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import utils.ThreadLocalUtil;

import java.util.List;

@Component
public class MemberValidator implements Validator {

    @Autowired
    private AccountServiceFeign accountServiceFeign;

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ProjectDao projectDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateMemberAccessInput.class)
                || clazz.equals(MemberClientInput.class)
                || clazz.equals(CreateMemberTeamInput.class)
                || clazz.equals(BindMemberGroupInput.class)
                || clazz.equals(BindMemberProjectInput.class)
                || clazz.equals(GroupMemberSearchInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateMemberAccessInput.class.isInstance(target)) {
            CreateMemberAccessInput createMemberAccessInput = (CreateMemberAccessInput) target;
            validatePassword(createMemberAccessInput.getPassword(), errors);
        } else if (MemberClientInput.class.isInstance(target)) {
            MemberClientInput memberClientInput = (MemberClientInput) target;
            validateClientId(memberClientInput.getClientId(), errors);
            validateAccountName(memberClientInput.getAccountName(), "name", errors);
        } else if (CreateMemberTeamInput.class.isInstance(target)) {
            CreateMemberTeamInput createMemberTeamInput = (CreateMemberTeamInput) target;
            teamValidaor.validateId(createMemberTeamInput.getTeamId(), "teamId", errors);
            validateMemberTeamInputList(createMemberTeamInput.getMemberTeamInputs(), errors);
        } else if (BindMemberGroupInput.class.isInstance(target)) {
            BindMemberGroupInput bindMemberGroupInput = (BindMemberGroupInput) target;
            teamValidaor.validateId(bindMemberGroupInput.getTeamId(), "teamId", errors);
            validateMemberGroupInputList(bindMemberGroupInput.getMemberGroupInputs(), errors);
            validateGroupId(bindMemberGroupInput.getGroupId(), errors);
        } else if (BindMemberProjectInput.class.isInstance(target)) {
            BindMemberProjectInput bindMemberProjectInput = (BindMemberProjectInput) target;
            teamValidaor.validateId(bindMemberProjectInput.getTeamId(), "teamId", errors);
            validateMemberProjectInputList(bindMemberProjectInput.getMemberProjectInputs(), errors);
            validateProjectId(bindMemberProjectInput.getProjectId(), errors);
        } else if (GroupMemberSearchInput.class.isInstance(target)) {
            GroupMemberSearchInput groupMemberSearchInput = (GroupMemberSearchInput) target;
            groupValidator.validateId(groupMemberSearchInput.getGroupId(), "groupId", errors);
            validateAccountName(groupMemberSearchInput.getAccountName(), "accountName", errors);
        } else {
            errors.rejectValue("password", "", "数据绑定错误");
        }
    }

    public void validatePassword(String password, Errors errors) {

        Guest guest = ThreadLocalUtil.getGuest();
        ValidateAccountInput validateAccountInput = new ValidateAccountInput();
        validateAccountInput.setId(guest.getId());
        validateAccountInput.setPassword(password);

        try {
            accountServiceFeign.validateAccount(validateAccountInput);
        } catch (Exception e) {
            errors.rejectValue("password", String.valueOf(NestStatusCode.MEMBER_PASSWORD_ERROR), "密码错误");
        }
    }

    public void validateClientId(String clientId, Errors errors) {

        if (StringUtils.isEmpty(clientId)) {
            errors.rejectValue("clientId", String.valueOf(NestStatusCode.MEMBER_CLIENTID_IS_EMPTY), "客户端id为空");
        }
    }

    public void validateAccountName(String name, String nameField, Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue(nameField, String.valueOf(NestStatusCode.MEMBER_NAME_IS_EMPTY));
        }
    }

    public void validateMemberTeamInputList(List<MemberTeamInput> list, Errors errors) {

        if (list == null || list.size() == 0) {
            errors.rejectValue("memberTeamInputs", String.valueOf(NestStatusCode.MEMBER_LIST_IS_EMPTY), "添加成员数为空");
        }

        list.forEach(memberTeamInput -> {

            if (memberTeamInput.getId() == null) {
                errors.rejectValue("memberTeamInputs", String.valueOf(NestStatusCode.MEMBER_ACCOUNT_ID_IS_EMPTY), "添加成员的id为空");
            }

            if (memberTeamInput.getRole() == null) {
                errors.rejectValue("memberTeamInputs", String.valueOf(NestStatusCode.MEMBER_ACCOUNT_ROLE_IS_EMPTY), "添加成员的角色为空");
            }
        });
    }

    public void validateMemberGroupInputList(List<MemberGroupInput> list, Errors errors) {

        if (list == null || list.size() == 0) {
            errors.rejectValue("memberGroupInputs", String.valueOf(NestStatusCode.MEMBER_LIST_IS_EMPTY), "添加成员数为空");
        }

        list.forEach(memberGroupInput -> {
            Member member = memberDao.getMemberById(memberGroupInput.getMemberId());

            if (member == null) {
                errors.rejectValue("memberGroupInputs", String.valueOf(NestStatusCode.TEAM_NOT_EXIST));
                return;
            }
        });
    }

    public void validateMemberProjectInputList(List<MemberProjectInput> list, Errors errors) {

        if (list == null || list.size() == 0) {
            errors.rejectValue("memberProjectInputs", String.valueOf(NestStatusCode.MEMBER_LIST_IS_EMPTY), "添加成员数为空");
        }

        list.forEach(memberProjectInput -> {
            Member member = memberDao.getMemberById(memberProjectInput.getMemberId());

            if (member == null) {
                errors.rejectValue("memberProjectInputs", String.valueOf(NestStatusCode.TEAM_NOT_EXIST));
                return;
            }
        });
    }

    public void validateGroupId(Integer groupId, Errors errors) {

        if (groupId == null) {
            errors.rejectValue("groupId", String.valueOf(NestStatusCode.GROUP_ID_IS_EMPTY));
            return;
        }

        Group group = groupDao.getGroupById(groupId);

        if (group == null) {
            errors.rejectValue("groupId", String.valueOf(NestStatusCode.GROUP_NOT_EXIST));
        }
    }

    public void validateProjectId(Integer projectId, Errors errors) {

        if (projectId == null) {
            errors.rejectValue("projectId", String.valueOf(NestStatusCode.PROJECT_ID_IS_EMPTY));
            return;
        }

        Project project = projectDao.getProjectById(projectId);

        if (project == null) {
            errors.rejectValue("projectId", String.valueOf(NestStatusCode.PROJECT_NOT_EXIST));
        }
    }
}
