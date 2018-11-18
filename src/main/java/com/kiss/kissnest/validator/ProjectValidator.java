package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.input.CreateProjectInput;
import com.kiss.kissnest.input.CreateProjectRepositoryInput;
import com.kiss.kissnest.input.UpdateProjectInput;
import com.kiss.kissnest.status.NestStatusCode;
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
                clazz.equals(UpdateProjectInput.class) ||
                clazz.equals(CreateProjectRepositoryInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateProjectInput.class.isInstance(target)) {
            CreateProjectInput createProjectInput = (CreateProjectInput) target;
            Integer teamId = createProjectInput.getTeamId();
            boolean teamIdValidated = teamValidaor.validateId(teamId, "teamId", errors);

            if (teamIdValidated) {
                validateCreateName(createProjectInput.getName(), teamId, errors);
                validateSlug(createProjectInput.getSlug(), teamId, errors);
            }

            validateType(createProjectInput.getType(), errors);
        } else if (UpdateProjectInput.class.isInstance(target)) {
            UpdateProjectInput updateProjectInput = (UpdateProjectInput) target;

            Integer teamId = updateProjectInput.getTeamId();
            boolean teamIdVal = teamValidaor.validateId(teamId, "teamId", errors);
            boolean projectVal = validateId(updateProjectInput.getId(),"id",errors);

            if (teamIdVal && projectVal) {
                validateUpdateName(updateProjectInput.getName(), teamId,updateProjectInput.getId(), errors);
            }

            groupValidator.validateId(updateProjectInput.getGroupId(), "groupId", errors);
            validateId(updateProjectInput.getId(),"id", errors);
            validateType(updateProjectInput.getType(), errors);
        } else if (CreateProjectRepositoryInput.class.isInstance(target)) {
            CreateProjectRepositoryInput createProjectRepositoryInput = (CreateProjectRepositoryInput) target;
            validateId(createProjectRepositoryInput.getProjectId(),"projectId",errors);
        }
    }

    public void validateCreateName(String name, Integer teamId, Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_IS_EMPTY), "项目名称不能为空");
            return;
        }

        Project project = projectDao.getProjectByNameAndTeamId(name, teamId);

        if (project != null) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_EXIST), "项目名称已存在");
        }
    }

    public void validateUpdateName(String name, Integer teamId,Integer id, Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_IS_EMPTY), "项目名称不能为空");
            return;
        }

        Project project = projectDao.getProjectByNameAndTeamId(name, teamId);

        if (project != null && id != project.getId()) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_EXIST), "项目名称已存在");
        }
    }

    public void validateType(Integer type, Errors errors) {

        if (type == null) {
            errors.rejectValue("type", String.valueOf(NestStatusCode.PROJECT_TYPE_IS_EMPTY), "项目类型不能为空");
        }
    }

    public void validateSlug(String slug, Integer teamId, Errors errors) {

        if (StringUtils.isEmpty(slug)) {
            errors.rejectValue("slug", String.valueOf(NestStatusCode.PROJECT_SLUG_EMPTY), "项目路径不能为空");
            return;
        }

        Project project = projectDao.getProjectBySlugAndTeamId(slug, teamId);

        if (project != null) {
            errors.rejectValue("slug", String.valueOf(NestStatusCode.PROJECT_SLUG_IS_EXIST), "项目路径已存在");
        }
    }

    public boolean validateId(Integer id,String idName, Errors errors) {

        if (id == null) {
            errors.rejectValue(idName, String.valueOf(NestStatusCode.PROJECT_ID_IS_EMPTY), "项目id不能为空");
            return false;
        }

        Project project = projectDao.getProjectById(id);

        if (project == null) {
            errors.rejectValue(idName, String.valueOf(NestStatusCode.PROJECT_ID_NOT_EXIST), "项目id不存在");
            return false;
        }

        return true;
    }
}
