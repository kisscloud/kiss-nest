package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.kiss.foundation.utils.GuestUtil;

import java.util.List;

@Component
public class ProjectValidator implements Validator {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private GroupValidator groupValidator;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Autowired
    private MemberDao memberDao;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(CreateProjectInput.class) ||
                clazz.equals(UpdateProjectInput.class) ||
                clazz.equals(CreateProjectRepositoryInput.class) ||
                clazz.equals(CreateTagInput.class) ||
                clazz.equals(QueryProjectInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateProjectInput.class.isInstance(target)) {
            CreateProjectInput createProjectInput = (CreateProjectInput) target;
            Integer teamId = createProjectInput.getTeamId();
            boolean teamIdValidated = teamValidaor.validateId(teamId, "teamId", errors);
            Integer groupId = createProjectInput.getGroupId();
            boolean groupIdValidated = groupValidator.validateId(groupId, "groupId", errors);

            if (teamIdValidated && groupIdValidated) {
                validateCreateName(createProjectInput.getName(), teamId, groupId, errors);
                validateSlug(createProjectInput.getSlug(), teamId, groupId, errors);
            }

            validateType(createProjectInput.getType(), errors);
        } else if (UpdateProjectInput.class.isInstance(target)) {
            UpdateProjectInput updateProjectInput = (UpdateProjectInput) target;

            Integer teamId = updateProjectInput.getTeamId();
            boolean teamIdVal = teamValidaor.validateId(teamId, "teamId", errors);
            boolean projectVal = validateId(updateProjectInput.getId(), "id", errors);
            boolean groupVal = groupValidator.validateId(updateProjectInput.getGroupId(), "groupId", errors);
            if (teamIdVal && projectVal && groupVal) {
                validateUpdateName(updateProjectInput.getName(), updateProjectInput.getGroupId(), teamId, updateProjectInput.getId(), errors);
            }

            validateId(updateProjectInput.getId(), "id", errors);
            validateType(updateProjectInput.getType(), errors);
        } else if (CreateProjectRepositoryInput.class.isInstance(target)) {
            CreateProjectRepositoryInput createProjectRepositoryInput = (CreateProjectRepositoryInput) target;
            validateId(createProjectRepositoryInput.getProjectId(), "projectId", errors);
        } else if (CreateTagInput.class.isInstance(target)) {
            CreateTagInput createTagInput = (CreateTagInput) target;
            boolean projectVal = validateId(createTagInput.getProjectId(), "projectId", errors);

            if (projectVal) {
                ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(createTagInput.getProjectId());
                Member member = memberDao.getMemberByAccountId(GuestUtil.getGuestId());
                Integer repositoryId = projectRepository.getRepositoryId();
                validateBranch(createTagInput.getRef(), repositoryId, member.getAccessToken(), errors);
                validateTag(createTagInput.getTagName(), repositoryId, member.getAccessToken(), errors);
            }
        } else if (QueryProjectInput.class.isInstance(target)) {

        }
    }

    public void validateBranch(String ref, Integer repositoryId, String accessToken, Errors errors) {

        if (StringUtils.isEmpty(ref)) {
            errors.rejectValue("ref", String.valueOf(NestStatusCode.PROJECT_BRANCH_IS_EMPTY), "项目分支为空");
            return;
        }

        List<GitlabBranch> branchList = gitlabApiUtil.getBranches(repositoryId, accessToken);
        boolean branchExist = false;

        for (GitlabBranch gitlabBranch : branchList) {
            if (gitlabBranch.getName().equals(ref)) {
                branchExist = true;
                break;
            }
        }

        if (!branchExist) {
            errors.rejectValue("ref", String.valueOf(NestStatusCode.PROJECT_BRANCH_NOT_EXIST), "项目分支不存在");
        }
    }

    public void validateTag(String tagName, Integer repositoryId, String accessToken, Errors errors) {

        if (StringUtils.isEmpty(tagName)) {
            errors.rejectValue("tagName", String.valueOf(NestStatusCode.PROJECT_TAG_IS_EMPTY), "项目版本号为空");
            return;
        }

        List<GitlabTag> gitlabTagList = gitlabApiUtil.getTags(repositoryId, accessToken);
        boolean tagExist = false;

        for (GitlabTag gitlabTag : gitlabTagList) {
            if (gitlabTag.getName().equals(tagName)) {
                tagExist = true;
            }
        }

        if (tagExist) {
            errors.rejectValue("tagName", String.valueOf(NestStatusCode.PROJECT_TAG_NOT_EXIST), "项目版本号已存在");
        }
    }

    public void validateCreateName(String name, Integer teamId, Integer groupId, Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_IS_EMPTY), "项目名称不能为空");
            return;
        }

        Project project = projectDao.getProjectByNameAndGroupIdAndTeamId(name, groupId, teamId);

        if (project != null) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_EXIST), "项目名称已存在");
        }
    }

    public void validateUpdateName(String name, Integer groupId, Integer teamId, Integer id, Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_IS_EMPTY), "项目名称不能为空");
            return;
        }

        Project project = projectDao.getProjectByNameAndGroupIdAndTeamId(name, groupId, teamId);

        if (project != null && id != project.getId()) {
            errors.rejectValue("name", String.valueOf(NestStatusCode.PROJECT_NAME_EXIST), "项目名称已存在");
        }
    }

    public void validateType(Integer type, Errors errors) {

        if (type == null) {
            errors.rejectValue("type", String.valueOf(NestStatusCode.PROJECT_TYPE_IS_EMPTY), "项目类型不能为空");
        }
    }

    public void validateSlug(String slug, Integer teamId, Integer groupId, Errors errors) {

        if (StringUtils.isEmpty(slug)) {
            errors.rejectValue("slug", String.valueOf(NestStatusCode.PROJECT_SLUG_EMPTY), "项目路径不能为空");
            return;
        }

        Project project = projectDao.getProjectBySlugAndGroupIdAndTeamId(slug, groupId, teamId);

        if (project != null) {
            errors.rejectValue("slug", String.valueOf(NestStatusCode.PROJECT_SLUG_IS_EXIST), "项目路径已存在");
        }
    }

    public boolean validateId(Integer id, String idName, Errors errors) {

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
