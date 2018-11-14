package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.input.CreateProjectInput;
import com.kiss.kissnest.input.UpdateProjectInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProjectValidator implements Validator {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private GroupValidator groupValidator;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(CreateProjectInput.class) ||
                clazz.equals(UpdateProjectInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateProjectInput.class.isInstance(target)) {
            CreateProjectInput createProjectInput = (CreateProjectInput) target;
            Integer teamId = createProjectInput.getTeamId();
            boolean teamIdValidated = teamValidaor.validateId(teamId,"teamId",errors);

            if (teamIdValidated) {
                validateName(createProjectInput.getName(),teamId,errors);
                validateSlug(createProjectInput.getSlug(),teamId,errors);
            }

            validateType(createProjectInput.getType(),errors);
        } else if (UpdateProjectInput.class.isInstance(target)) {
            UpdateProjectInput updateProjectInput = (UpdateProjectInput) target;

            Integer teamId = updateProjectInput.getTeamId();
            boolean teamIdValidated = teamValidaor.validateId(teamId,"teamId",errors);

            if (teamIdValidated) {
                validateName(updateProjectInput.getName(),teamId,errors);
            }

            groupValidator.validateId(updateProjectInput.getGroupId(),"groupId",errors);
            validateId(updateProjectInput.getId(),errors);
            validateType(updateProjectInput.getType(),errors);
        }
    }

    public void validateName (String name,Integer teamId,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name","","项目名称不能为空");
            return;
        }

        Project project = projectDao.getProjectByNameAndTeamId(name,teamId);

        if (project != null) {
            errors.rejectValue("name","","项目名称已存在");
        }
    }

    public void validateType (Integer type,Errors errors) {

        if (type == null) {
            errors.rejectValue("type","","项目类型不能为空");
        }
    }

    public void validateSlug (String slug,Integer teamId,Errors errors) {

        if (StringUtils.isEmpty(slug)) {
            errors.rejectValue("slug","","项目路径不能为空");
            return;
        }

        Project project = projectDao.getProjectBySlugAndTeamId(slug,teamId);

        if (project != null) {
            errors.rejectValue("slug","","项目路径已存在");
        }
    }

    public void validateId (Integer id,Errors errors) {

        if (id == null) {
            errors.rejectValue("id","","项目id不能为空");
            return;
        }

        Project project = projectDao.getProjectById(id);

        if (project == null) {
            errors.rejectValue("id","","项目id不存在");
        }
    }
}
