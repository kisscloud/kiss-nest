package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.CreateProjectInput;
import com.kiss.kissnest.output.ProjectOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.gitlab.api.models.GitlabProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.ThreadLocalUtil;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private MemberDao memberDao;

    public ResultOutput createProject(CreateProjectInput createProjectInput) {

        Project project = (Project) BeanCopyUtil.copy(createProjectInput,Project.class);

        Integer count = projectDao.createProject(project);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_PROJECT_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(project,ProjectOutput.class));
    }

    public ResultOutput deleteProject(Integer id) {

        Project project = projectDao.getProjectById(id);

        if (project == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        Integer count = projectDao.deleteProjectById(id);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.DELETE_PROJECT_FAILED);
        }

        return ResultOutputUtil.success();
    }

    public ResultOutput updateProject(Project project) {

        Integer count = projectDao.updateProject(project);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_PROJECT_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(project,ProjectOutput.class));
    }

    public ResultOutput getProjectById(Integer id) {

        Project project = projectDao.getProjectById(id);

        return ResultOutputUtil.success(BeanCopyUtil.copy(project,ProjectOutput.class));
    }

    public ResultOutput getProjects(Integer teamId) {

        List<Project> projects = projectDao.getProjects(teamId);

        return ResultOutputUtil.success(BeanCopyUtil.copyList(projects,ProjectOutput.class));
    }

    public ResultOutput createProjectRepository(Integer projectId) {

        Project project = projectDao.getProjectById(projectId);

        if (project == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        if (StringUtils.isEmpty(project.getSlug())) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_SLUG_EMPTY);
        }

        Group group = groupDao.getGroupById(project.getGroupId());

        if (group == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_MASTER_GROUP_NOT_EXIST);
        }

        if (group.getRepositoryId() == null) {
            return ResultOutputUtil.error(NestStatusCode.GROUP_REPOSITORYID_NOT_EXIST);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
        GitlabProject gitlabProject =gitlabApiUtil.createProjectForGroup(project.getSlug(),group.getRepositoryId(),accessToken);

        if (gitlabProject == null) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_PROJECT_REPOSITORY_FAILED);
        }

        project.setRepositoryId(gitlabProject.getId());

        projectDao.addRepositoryIdById(project);

        return ResultOutputUtil.success();
    }
}
