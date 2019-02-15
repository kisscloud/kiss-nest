package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.input.CreateProjectRepositoryInput;
import com.kiss.kissnest.output.ProjectRepositoryOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.foundation.exception.StatusException;
import com.kiss.foundation.utils.BeanCopyUtil;
import com.kiss.foundation.utils.ThreadLocalUtil;
import org.gitlab.api.models.GitlabProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectRepositoryService {

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    @Autowired
    private OperationLogService operationLogService;

    public ProjectRepositoryOutput createProjectRepository(Integer projectId) {

        Project project = projectDao.getProjectById(projectId);

        if (project == null) {
            throw new StatusException(NestStatusCode.PROJECT_NOT_EXIST);
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        if (projectRepository != null) {
            throw new StatusException(NestStatusCode.PROJECT_REPOSITORY_EXIST);
        }

        if (StringUtils.isEmpty(project.getSlug())) {
            throw new StatusException(NestStatusCode.PROJECT_SLUG_EMPTY);
        }

        Group group = groupDao.getGroupById(project.getGroupId());

        if (group == null) {
            throw new StatusException(NestStatusCode.PROJECT_MASTER_GROUP_NOT_EXIST);
        }

        if (group.getRepositoryId() == null) {
            throw new StatusException(NestStatusCode.GROUP_REPOSITORYID_NOT_EXIST);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
        GitlabProject gitlabProject = gitlabApiUtil.createProjectForGroup(project.getSlug(), group.getRepositoryId(), accessToken);

        if (gitlabProject == null) {
            throw new StatusException(NestStatusCode.CREATE_PROJECT_REPOSITORY_FAILED);
        }

        projectRepository = new ProjectRepository();
        projectRepository.setHttpUrl(gitlabProject.getHttpUrl());
        projectRepository.setName(gitlabProject.getName());
        projectRepository.setProjectId(projectId);
        projectRepository.setRepositoryId(gitlabProject.getId());
        projectRepository.setSshUrl(gitlabProject.getSshUrl());
        projectRepository.setTeamId(project.getTeamId());
        projectRepository.setOperatorId(ThreadLocalUtil.getGuest().getId());
        projectRepository.setOperatorName(ThreadLocalUtil.getGuest().getName());
        projectRepository.setBranchCount(1);
        projectRepository.setCommitCount(0);
        projectRepository.setMemberCount(1);
        projectRepository.setMergeRequestCount(0);
        projectRepository.setPathWithNamespace(gitlabProject.getPathWithNamespace());
        projectRepository.setProjectName(project.getName());

        projectRepositoryDao.createProjectRepository(projectRepository);

        operationLogService.saveOperationLog(projectRepository.getTeamId(), ThreadLocalUtil.getGuest(), null, projectRepository, "id", OperationTargetType.TYPE__CREATE_PROJECT_REPOSITORY);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(), projectRepository.getTeamId(), null, projectRepository.getProjectId(), OperationTargetType.TYPE__CREATE_PROJECT_REPOSITORY, projectRepository);
        return BeanCopyUtil.copy(projectRepository, ProjectRepositoryOutput.class);
    }

    public List<ProjectRepositoryOutput> getProjectRepositoriesByTeamId(Integer teamId) {

        List<ProjectRepository> projectRepositoryList = projectRepositoryDao.getProjectRepositoriesByTeamId(teamId);

        return BeanCopyUtil.copyList(projectRepositoryList, ProjectRepositoryOutput.class);
    }

    public Boolean validateProjectRepositoryExist(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        if (projectRepository == null) {
            return false;
        }

        return true;
    }

    public ProjectRepositoryOutput getProjectRepositoryByProjectId(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        return BeanCopyUtil.copy(projectRepository, ProjectRepositoryOutput.class, BeanCopyUtil.defaultFieldNames);
    }
}
