package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.entity.OperationTargetType;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.input.CreateProjectInput;
import com.kiss.kissnest.input.UpdateProjectInput;
import com.kiss.kissnest.output.ProjectOutput;
import com.kiss.kissnest.output.ProjectTypeOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.CodeUtil;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import entity.Guest;
import org.gitlab.api.models.GitlabBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.BeanCopyUtil;
import utils.ThreadLocalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private CodeUtil codeUtil;

    @Value("${project.type}")
    private String projectTypes;

    @Autowired
    private OperationLogService operationLogService;

    public ResultOutput createProject(CreateProjectInput createProjectInput) {

        Project project = (Project) BeanCopyUtil.copy(createProjectInput, Project.class);
        Guest guest = ThreadLocalUtil.getGuest();
        project.setMembersCount(1);
        project.setOperatorId(guest.getId());
        project.setOperatorName(guest.getName());
        Integer count = projectDao.createProject(project);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_PROJECT_FAILED);
        }

        groupDao.addCount(project.getTeamId(), project.getGroupId(), "projects", 1);
        ProjectOutput projectOutput = (ProjectOutput) BeanCopyUtil.copy(project, ProjectOutput.class);
        projectOutput.setTypeText(codeUtil.getEnumsMessage("project.type", String.valueOf(projectOutput.getType())));
//        operationLogService.saveOperationLog(project.getTeamId(),guest,null,project,"id",OperationTargetType.TYPE_CREATE_PROJECT);
        return ResultOutputUtil.success(projectOutput);
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

//        operationLogService.saveOperationLog(project.getTeamId(),ThreadLocalUtil.getGuest(),project,null,"id",OperationTargetType.TYPE_DELETE_PROJECT);

        return ResultOutputUtil.success();
    }

    public ResultOutput updateProject(UpdateProjectInput updateProjectInput) {

        Project project = (Project) BeanCopyUtil.copy(updateProjectInput, Project.class);
        Project oldValue = projectDao.getProjectById(updateProjectInput.getId());
        Guest guest = ThreadLocalUtil.getGuest();
        project.setOperatorId(guest.getId());
        project.setOperatorName(guest.getName());
        Integer count = projectDao.updateProject(project);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_PROJECT_FAILED);
        }

//        operationLogService.saveOperationLog(project.getTeamId(),guest,oldValue,project,"id",OperationTargetType.TYPE_UPDATE_PROJECT);

        return ResultOutputUtil.success(BeanCopyUtil.copy(project, ProjectOutput.class));
    }

    public ResultOutput getProjectById(Integer id) {

        Project project = projectDao.getProjectById(id);

        return ResultOutputUtil.success(BeanCopyUtil.copy(project, ProjectOutput.class));
    }

    public ResultOutput getProjects(Integer teamId, Integer groupId) {

        List<Project> projects = projectDao.getProjects(teamId, groupId);
        List<ProjectOutput> projectOutputs = (List) BeanCopyUtil.copyList(projects, ProjectOutput.class, BeanCopyUtil.defaultFieldNames);

        projectOutputs.forEach((projectOutput -> projectOutput.setTypeText(codeUtil.getEnumsMessage("project.type", String.valueOf(projectOutput.getType())))));

        return ResultOutputUtil.success(projectOutputs);
    }

    public ResultOutput getProjectBranches(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        if (projectRepository == null || projectRepository.getRepositoryId() == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
        List<GitlabBranch> gitlabBranches = gitlabApiUtil.getBranches(projectRepository.getRepositoryId(), accessToken);
        List<String> branches = new ArrayList<>();

        if (gitlabBranches != null) {

            for (GitlabBranch gitlabBranch : gitlabBranches) {

                if (!StringUtils.isEmpty(gitlabBranch.getName())) {
                    branches.add(gitlabBranch.getName());
                }
            }
        }

        return ResultOutputUtil.success(branches);
    }

    public ResultOutput getProjectsWithoutBuildJob(Integer teamId) {

        List<Project> projects = projectDao.getProjectsWithoutBuildJob(teamId);
        List<ProjectOutput> projectOutputs = (List) BeanCopyUtil.copyList(projects, ProjectOutput.class);

        return ResultOutputUtil.success(projectOutputs);
    }

    public ResultOutput getBuildProjects(Integer teamId) {

        List<Project> projects = projectDao.getProjectsWithBuildJob(teamId);
        List<ProjectOutput> projectOutputs = (List) BeanCopyUtil.copyList(projects, ProjectOutput.class);


//        if (projects != null && projects.size() != 0) {
//            String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
//
//            if (!StringUtils.isEmpty(accessToken)) {
//                List<String> branches = new ArrayList<>();
//
//                for (Project project : projects) {
//                    Integer repositoryId = project.getRepositoryId();
//
//                    if (repositoryId != null) {
//                        List<GitlabBranch> gitlabBranches = gitlabApiUtil.getBranches(repositoryId, accessToken);
//
//                        for (GitlabBranch gitlabBranch : gitlabBranches) {
//                            branches.add(gitlabBranch.getName());
//                        }
//                    }
//
//                    ProjectOutput projectOutput = new ProjectOutput();
//                    projectOutput.setId(project.getId());
//                    projectOutput.setBranches(branches);
//                    projectOutput.setName(project.getName());
//                    projectOutputs.add(projectOutput);
//                }
//            }
//        }
        return ResultOutputUtil.success(projectOutputs);
    }

    public ResultOutput getProjectTypes() {
        String[] types = projectTypes.split(",");
        List<ProjectTypeOutput> typeList = new ArrayList<>();

        for (String type : types) {
            ProjectTypeOutput projectTypeOutput = new ProjectTypeOutput();
            projectTypeOutput.setId(Integer.valueOf(type));
            projectTypeOutput.setName(codeUtil.getEnumsMessage("project.type", type));
            typeList.add(projectTypeOutput);
        }

        return ResultOutputUtil.success(typeList);
    }
}
