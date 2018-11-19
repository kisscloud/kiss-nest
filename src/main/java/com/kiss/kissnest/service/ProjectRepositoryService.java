package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.OperationTargetType;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.input.CreateProjectRepositoryInput;
import com.kiss.kissnest.output.ProjectRepositoryOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.gitlab.api.models.GitlabProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.BeanCopyUtil;
import utils.ThreadLocalUtil;

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

    public ResultOutput createProjectRepository(CreateProjectRepositoryInput createProjectRepositoryInput) {

        Integer projectId = createProjectRepositoryInput.getProjectId();
        Project project = projectDao.getProjectById(projectId);

        if (project == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        if (projectRepository != null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_REPOSITORY_EXIST);
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
        GitlabProject gitlabProject = gitlabApiUtil.createProjectForGroup(project.getSlug(), group.getRepositoryId(), accessToken);

        if (gitlabProject == null) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_PROJECT_REPOSITORY_FAILED);
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
        projectRepository.setPathWithNamespace(gitlabProject.getPathWithNamespace());
        projectRepository.setProjectName(project.getName());

        projectRepositoryDao.createProjectRepository(projectRepository);

        operationLogService.saveOperationLog(projectRepository.getTeamId(),ThreadLocalUtil.getGuest(),null,projectRepository,"id",OperationTargetType.TYPE__CREATE_PROJECT_REPOSITORY);
        return ResultOutputUtil.success(BeanCopyUtil.copy(projectRepository,ProjectRepositoryOutput.class));
    }

    public ResultOutput getProjectRepositoriesByTeamId(Integer teamId) {

        List<ProjectRepository> projectRepositoryList = projectRepositoryDao.getProjectRepositoriesByTeamId(teamId);

        return ResultOutputUtil.success(BeanCopyUtil.copyList(projectRepositoryList, ProjectRepositoryOutput.class));
    }

    public ResultOutput validateProjectRepositoryExist(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        Map<String, Object> result = new HashMap<>();

        if (projectRepository == null) {
            result.put("exist", false);
        } else {
            result.put("exist", true);
        }

        return ResultOutputUtil.success(result);
    }

    public ResultOutput getProjectRepositoryByProjectId(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        return ResultOutputUtil.success(BeanCopyUtil.copy(projectRepository, ProjectRepositoryOutput.class,BeanCopyUtil.defaultFieldNames));
    }
}
