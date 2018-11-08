package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.input.BindGroupProjectsInput;
import com.kiss.kissnest.input.CreateGroupInput;
import com.kiss.kissnest.input.UpdateGroupInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class GroupValidator implements Validator {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private ProjectDao projectDao;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(CreateGroupInput.class) ||
                clazz.equals(BindGroupProjectsInput.class) ||
                clazz.equals(UpdateGroupInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateGroupInput.class.isInstance(target)) {
            CreateGroupInput createGroupInput = (CreateGroupInput) target;
            Integer teamId = createGroupInput.getTeamId();
            boolean teamIdValidated = teamValidaor.validateId(teamId,"teamId",errors);

            if (teamIdValidated) {
                validateName(createGroupInput.getName(),teamId,errors);
                validateSlug(createGroupInput.getSlug(),teamId,errors);
            }

//            validateStatus(createGroupInput.getStatus(),errors);
        } else if (UpdateGroupInput.class.isInstance(target)) {
            UpdateGroupInput updateGroupInput = (UpdateGroupInput) target;
            Integer teamId = updateGroupInput.getTeamId();

            boolean teamIdValidated = teamValidaor.validateId(teamId,"teamId",errors);

            if (teamIdValidated) {
                validateName(updateGroupInput.getName(),teamId,errors);
            }

            validateId(updateGroupInput.getId(),"id",errors);
        } else if (BindGroupProjectsInput.class.isInstance(target)) {
            BindGroupProjectsInput bindGroupProjectsInput = (BindGroupProjectsInput) target;
            validateId(bindGroupProjectsInput.getGroupId(),"groupId",errors);
            validateProjectIds(bindGroupProjectsInput.getProjectIds(),errors);
        } else {
            errors.rejectValue("name","","数据绑定错误");
        }

    }

    public void validateName(String name,Integer teamId,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name","","项目组名不能为空");
            return;
        }

        Group group = groupDao.getGroupByNameAndTeamId(name,teamId);

        if (group != null) {
            errors.rejectValue("name","","项目组名已存在");
        }
    }

    public void validateId (Integer id,String idName,Errors errors) {

        if (id == null) {
            errors.rejectValue(idName,"","项目组id不能为空");
            return;
        }

        Group group = groupDao.getGroupById(id);

        if (group == null) {
            errors.rejectValue(idName,"","项目组id不存在");
        }
    }

    public void validateStatus (Integer status,Errors errors) {

        if (status == null) {
            errors.rejectValue("status","","项目组状态不能为空");
        }
    }

    public void validateProjectIds (List<Integer> projectIds,Errors errors) {

        if (projectIds == null || projectIds.size() == 0) {
            errors.rejectValue("projectIds","","项目id不能为空");
            return;
        }

        for (Integer projectId : projectIds) {
            Project project = projectDao.getProjectById(projectId);

            if (project == null) {
                errors.rejectValue("projectIds","","项目id不存在");
                return;
            }
        }

    }

    public void validateSlug (String slug,Integer teamId,Errors errors) {

        if (StringUtils.isEmpty(slug)) {
            errors.rejectValue("slug","","项目路径不能为空");
        }

        Group group = groupDao.getGroupBySlugAndTeamId(slug,teamId);

        if (group != null) {
            errors.rejectValue("slug","","项目路径已存在");
        }
    }
}
