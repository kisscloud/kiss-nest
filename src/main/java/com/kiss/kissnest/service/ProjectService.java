package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.*;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.LangUtil;
import com.kiss.foundation.entity.Guest;
import com.kiss.foundation.exception.StatusException;
import com.kiss.foundation.utils.BeanCopyUtil;
import com.kiss.foundation.utils.GuestUtil;
import com.kiss.foundation.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.gitlab.api.models.GitlabBranch;
import org.gitlab.api.models.GitlabBranchCommit;
import org.gitlab.api.models.GitlabRelease;
import org.gitlab.api.models.GitlabTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
    private JobDao jobDao;

    @Autowired
    private BuildLogDao buildLogDao;

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private LangUtil langUtil;

    @Value("${project.type}")
    private String projectTypes;

    @Value("${gitlab.server.url}")
    private String gitlabUrl;

    @Value("${gitlab.server.commitPath}")
    private String gitlabCommitPath;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private MemberProjectDao memberProjectDao;

    public ProjectOutput createProject(CreateProjectInput createProjectInput) {

        Project project = BeanCopyUtil.copy(createProjectInput, Project.class);
        Guest guest = ThreadLocalUtil.getGuest();
        project.setMembersCount(1);
        project.setOperatorId(guest.getId());
        project.setOperatorName(guest.getName());
        Integer count = projectDao.createProject(project);

        if (count == 0) {
            throw new StatusException(NestStatusCode.CREATE_PROJECT_FAILED);
        }

        teamDao.addCount("projects", 1, project.getTeamId());
        groupDao.addCount(project.getGroupId(), "projects", 1);
        Member member = memberDao.getMemberByAccountId(ThreadLocalUtil.getGuest().getId());
        memberDao.addCount(member.getId(), 1, "projects");

        MemberProject memberProject = new MemberProject();
        memberProject.setProjectId(project.getId());
        memberProject.setMemberId(member.getId());
        memberProject.setOperatorId(guest.getId());
        memberProject.setOperatorName(guest.getName());
        memberProject.setRole(1);
        memberProject.setTeamId(project.getTeamId());
        Integer memberGroupCount = memberProjectDao.createMemberProject(memberProject);

        if (memberGroupCount == 0) {
            throw new TransactionalException(NestStatusCode.CREATE_MEMBER_PROJECT_FAILED);
        }

        ProjectOutput projectOutput = projectDao.getProjectOutputById(project.getId());
        projectOutput.setTypeText(langUtil.getEnumsMessage("project.type", String.valueOf(projectOutput.getType())));
        Integer id = project.getId();
        project = projectDao.getProjectById(id);
        operationLogService.saveOperationLog(project.getTeamId(), guest, null, project, "id", OperationTargetType.TYPE_CREATE_PROJECT);
        operationLogService.saveDynamic(guest, project.getTeamId(), project.getGroupId(), project.getId(), OperationTargetType.TYPE_CREATE_PROJECT, project);
        return projectOutput;
    }

    @Transactional
    public void deleteProject(Integer id) {

        Project project = projectDao.getProjectById(id);

        if (project == null) {
            throw new StatusException(NestStatusCode.PROJECT_NOT_EXIST);
        }

        Integer count = projectDao.deleteProjectById(id);

        if (count == 0) {
            throw new StatusException(NestStatusCode.DELETE_PROJECT_FAILED);
        }

        buildLogDao.deleteBuildLogsByProjectId(project.getId());

        List<MemberProject> memberProjects = memberProjectDao.getMemberProjects(project.getTeamId(), id);

        for (MemberProject memberProject : memberProjects) {

            Integer memberCount = memberDao.deleteCount(memberProject.getMemberId(), 1, "projects");

            if (memberCount == 0) {
                throw new TransactionalException(NestStatusCode.DELETE_MEMBER_PROJECT_COUNT_FAILED);
            }
        }

        Integer memberProjectCount = memberProjectDao.deleteMemberProjectsByProjectId(id);

        if (memberProjectCount == 0) {
            throw new TransactionalException(NestStatusCode.DELETE_MEMBER_PROJECT_FAILED);
        }

        List<Job> jobs = jobDao.getJobByProjectId(project.getId());
        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (jobs != null && jobs.size() != 0) {
            jobs.forEach(job -> {
                Integer jobCount = jobDao.deleteJobById(job.getId());

                if (jobCount == 0) {
                    throw new TransactionalException(NestStatusCode.DELETE_JOB_FAILED);
                }

                jenkinsUtil.deleteJob(job.getJobName(), guest.getName(), member.getApiToken());
            });
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(id);

        if (projectRepository != null) {
            projectRepositoryDao.deleteProjectRepositoryById(projectRepository.getId());
            gitlabApiUtil.deleteProject(projectRepository.getRepositoryId(), member.getAccessToken());
        }

        operationLogService.saveOperationLog(project.getTeamId(), ThreadLocalUtil.getGuest(), project, null, "id", OperationTargetType.TYPE_DELETE_PROJECT);
        operationLogService.saveDynamic(guest, project.getTeamId(), project.getGroupId(), project.getId(), OperationTargetType.TYPE_DELETE_PROJECT, project);

    }

    public ProjectOutput updateProject(UpdateProjectInput updateProjectInput) {

        Project project = BeanCopyUtil.copy(updateProjectInput, Project.class);
        Project oldValue = projectDao.getProjectById(updateProjectInput.getId());
        Guest guest = ThreadLocalUtil.getGuest();
        project.setOperatorId(guest.getId());
        project.setOperatorName(guest.getName());
        Integer count = projectDao.updateProject(project);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_PROJECT_FAILED);
        }

        operationLogService.saveOperationLog(project.getTeamId(), guest, oldValue, project, "id", OperationTargetType.TYPE_UPDATE_PROJECT);
        operationLogService.saveDynamic(guest, project.getTeamId(), project.getGroupId(), project.getId(), OperationTargetType.TYPE_UPDATE_PROJECT, project);

        ProjectOutput projectOutput = BeanCopyUtil.copy(project, ProjectOutput.class);
        projectOutput.setTypeText(langUtil.getEnumsMessage("project.type", String.valueOf(project.getType())));

        return projectOutput;
    }

    public ProjectOutput getProjectById(Integer id) {

        Project project = projectDao.getProjectById(id);

        return BeanCopyUtil.copy(project, ProjectOutput.class);
    }

    public ProjectOutputs getProjects(QueryProjectInput queryProjectInput) {

        Integer start = (queryProjectInput.getPage() == 0 ? 1 : queryProjectInput.getPage() - 1) * queryProjectInput.getSize();
        queryProjectInput.setPage(start);

        List<ProjectOutput> projectOutputList = projectDao.getProjectOutputs(queryProjectInput);
        for (ProjectOutput projectOutput : projectOutputList) {
            projectOutput.setTypeText(langUtil.getEnumsMessage("project.type", String.valueOf(projectOutput.getType())));
            projectOutput.setLastBuildUrl(String.format("%s/commit/%s", projectOutput.getLastBuildUrl().replace(".git", ""), projectOutput.getLastBuild()));
            projectOutput.setLastDeployUrl(String.format("%s/commit/%s", projectOutput.getLastDeployUrl().replace(".git", ""), projectOutput.getLastDeploy()));
        }

        Integer count = projectDao.getProjectOutputsCount(queryProjectInput);

        return new ProjectOutputs(projectOutputList, count);
    }

    public List<String> getProjectBranches(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        if (projectRepository == null || projectRepository.getRepositoryId() == null) {
            throw new StatusException(NestStatusCode.PROJECT_NOT_EXIST);
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

        return branches;
    }

    public List<TagOutput> getProjectTags(Integer projectId) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        if (projectRepository == null || projectRepository.getRepositoryId() == null) {
            throw new StatusException(NestStatusCode.PROJECT_NOT_EXIST);
        }

        String accessToken = memberDao.getAccessTokenByAccountId(ThreadLocalUtil.getGuest().getId());
        List<GitlabTag> gitlabTags = gitlabApiUtil.getTags(projectRepository.getRepositoryId(), accessToken);
        List<TagOutput> branches = new ArrayList<>();

        if (gitlabTags != null) {

            for (GitlabTag gitlabTag : gitlabTags) {

                if (!StringUtils.isEmpty(gitlabTag.getName())) {
                    TagOutput tagOutput = new TagOutput();
                    GitlabBranchCommit gitlabBranchCommit = gitlabTag.getCommit();
                    // @TODO 为啥打了版本了 Release 还会为 null 呢
                    // GitlabRelease gitlabRelease = gitlabTag.getRelease();

                    tagOutput.setTagName(gitlabTag.getName());
                    tagOutput.setDescription(gitlabTag.getMessage());
                    tagOutput.setMessage(gitlabTag.getMessage());
                    tagOutput.setCreatedAt(gitlabBranchCommit.getCommittedDate().getTime());


                    branches.add(tagOutput);
                }
            }
        }

        log.info("gitlabTags：" + gitlabTags);
        return branches;
    }

    public List<ProjectOutput> getProjectsWithoutBuildJob(Integer teamId) {

        List<Project> projects = projectDao.getProjectsWithoutBuildJob(teamId);
        List<ProjectOutput> projectOutputs = BeanCopyUtil.copyList(projects, ProjectOutput.class);

        return projectOutputs;
    }

    public List<ProjectOutput> getProjectsWithBuildJobByTeamId(Integer teamId) {

        List<Project> projects = projectDao.getProjectsWithBuildJobByTeamId(teamId);
        List<ProjectOutput> projectOutputs = BeanCopyUtil.copyList(projects, ProjectOutput.class);

        return projectOutputs;
    }

    public List<ProjectOutput> getProjectsWithBuildJob(Integer teamId) {

        List<Project> projects = projectDao.getProjectsWithBuildJob(teamId);
        List<ProjectOutput> projectOutputs = BeanCopyUtil.copyList(projects, ProjectOutput.class);

        return projectOutputs;
    }

    public List<ProjectOutput> getProjectsWithDeployJob(Integer teamId) {

        List<Project> projects = projectDao.getProjectsWithDeployJob(teamId);
        List<ProjectOutput> projectOutputs = BeanCopyUtil.copyList(projects, ProjectOutput.class);

        return projectOutputs;
    }

    public List<ProjectTypeOutput> getProjectTypes() {
        String[] types = projectTypes.split(",");
        List<ProjectTypeOutput> typeList = new ArrayList<>();

        for (String type : types) {
            ProjectTypeOutput projectTypeOutput = new ProjectTypeOutput();
            projectTypeOutput.setId(Integer.valueOf(type));
            projectTypeOutput.setName(langUtil.getEnumsMessage("project.type", type));
            typeList.add(projectTypeOutput);
        }

        return typeList;
    }

    public TagOutput addTag(CreateTagInput createTagInput) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(createTagInput.getProjectId());
        Integer repositoryId = projectRepository.getRepositoryId();
        Member member = memberDao.getMemberByAccountId(GuestUtil.getGuestId());
        GitlabTag gitlabTag = gitlabApiUtil.addTag(repositoryId, createTagInput.getTagName(), createTagInput.getRef(), createTagInput.getMessage(), createTagInput.getReleaseDescription(), member.getAccessToken());

        if (gitlabTag == null) {
            throw new StatusException(NestStatusCode.CREATE_PROJECT_TAG_FAILED);
        }

        operationLogService.saveOperationLog(projectRepository.getTeamId(), ThreadLocalUtil.getGuest(), null, createTagInput, "id", OperationTargetType.TYPE__CREATE_PROJECT_TAG);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(), projectRepository.getTeamId(), null, projectRepository.getProjectId(), OperationTargetType.TYPE__CREATE_PROJECT_TAG, createTagInput);

        TagOutput tagOutput = new TagOutput();
        GitlabBranchCommit gitlabBranchCommit = gitlabTag.getCommit();
        GitlabRelease gitlabRelease = gitlabTag.getRelease();
        tagOutput.setTagName(gitlabRelease.getTagName());
        tagOutput.setDescription(gitlabRelease.getDescription());
        tagOutput.setMessage(gitlabTag.getMessage());
        tagOutput.setCreatedAt(gitlabBranchCommit.getCommittedDate().getTime());

        return tagOutput;
    }

    public void createBranch(CreateBranchInput createBranchInput) {

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(createBranchInput.getProjectId());
        Integer repositoryId = projectRepository.getRepositoryId();
        Member member = memberDao.getMemberByAccountId(GuestUtil.getGuestId());
        gitlabApiUtil.createBranch(repositoryId, createBranchInput.getBranchName(), createBranchInput.getRef(), member.getAccessToken());
        operationLogService.saveOperationLog(projectRepository.getTeamId(), ThreadLocalUtil.getGuest(), null, createBranchInput, "id", OperationTargetType.TYPE__CREATE_PROJECT_Branch);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(), projectRepository.getTeamId(), null, projectRepository.getProjectId(), OperationTargetType.TYPE__CREATE_PROJECT_Branch, createBranchInput);
    }

    public List<MemberOutput> getMemberProjectsByProjectId(Integer projectId) {

        List<Member> members = memberProjectDao.getMemberProjectsByProjectId(projectId);
        List<MemberOutput> memberOutputs = BeanCopyUtil.copyList(members, MemberOutput.class, BeanCopyUtil.defaultFieldNames);

        return memberOutputs;
    }
}
