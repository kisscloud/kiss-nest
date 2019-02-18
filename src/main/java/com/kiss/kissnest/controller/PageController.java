package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Link;
import com.kiss.kissnest.input.QueryProjectInput;
import com.kiss.kissnest.output.*;
import com.kiss.kissnest.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Page", description = "控制台页面接口")
@RequestMapping("/page")
public class PageController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JobService buildService;

    @Autowired
    private ProjectRepositoryService projectRepositoryService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private MemberService memberService;


    @ApiOperation(value = "获取项目组页面参数")
    @GetMapping("/groups")
    public Map<String, Object> GetPageGroupsParams(Integer teamId) {

        List<GroupOutput> groups = groupService.getGroups(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("groups", groups);
        result.put("status", groups);

        return result;
    }

    @ApiOperation(value = "获取项目页面参数")
    @PostMapping("/projects")
    public Map<String, Object> GetPageProjectsParams(@Validated @RequestBody QueryProjectInput queryProjectInput) {

        ProjectOutputs projects = projectService.getProjects(queryProjectInput);
        List<GroupOutput> groups = groupService.getGroups(queryProjectInput.getTeamId());
        List<ProjectTypeOutput> projectTypes = projectService.getProjectTypes();

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects);
        result.put("groups", groups);
        result.put("projectTypes", projectTypes);

        return result;
    }


    @ApiOperation(value = "获取构建记录页面参数")
    @GetMapping("/build/logs")
    public Map<String, Object> GetPageBuildLogsParams(Integer teamId) {

        List<ProjectOutput> projects = projectService.getProjectsWithoutBuildJob(teamId);
        List<ProjectOutput> buildProjects = projectService.getProjectsWithBuildJob(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects);
        result.put("build", buildProjects);

        return result;
    }

    @ApiOperation(value = "获取构建任务页面参数")
    @GetMapping("/build/jobs")
    public Map<String, Object> GetPageBuildJobsParams(Integer teamId) {

        List<JobOutput> jobs = buildService.getJobsByTeamId(teamId, 1);
        List<ProjectOutput> projects = projectService.getProjectsWithoutBuildJob(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs);
        result.put("projects", projects);

        return result;
    }


//    @ApiOperation(value = "获取仓库页面参数")
//    @GetMapping("/repositories")
//    public Map<String, Object> GetPageRepositoriesParams(@RequestParam("teamId") Integer teamId, Integer groupId) {
//
//        List<ProjectOutput> projects = projectService.getProjects(teamId, groupId);
//        List<ProjectRepositoryOutput> repositories = projectRepositoryService.getProjectRepositoriesByTeamId(teamId);
//        Map<String, Object> result = new HashMap<>();
//        result.put("projects", projects);
//        result.put("repositories", repositories);
//
//        return result;
//    }

//    @ApiOperation(value = "获取部署记录页面参数")
//    @GetMapping("/deploy/logs")
//    public Map<String, Object> GetPageDeployLogsParams(Integer teamId) {
//
//        List<ProjectOutput> projects = projectService.getProjects(teamId, null);
//
//        Map<String, Object> result = new HashMap<>();
//
//        result.put("projects", projects);
//
//        return result;
//    }

//    @ApiOperation(value = "获取部署任务页面参数")
//    @GetMapping("/deploy/jobs")
//    public Map<String, Object> GetPageDeployJobsParams(Integer teamId) {
//
//        List<JobOutput> jobs = buildService.getJobsByTeamId(teamId, 2);
//        List<EnvironmentOutput> envs = serverService.getEnvironmentsByTeamId(teamId);
//        List<ProjectOutput> projects = projectService.getProjectsWithBuildJobByTeamId(teamId);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("jobs", jobs);
//        result.put("envs", envs);
//        result.put("projects", projects);
//
//        return result;
//    }

    @ApiOperation(value = "获取设置页面参数")
    @GetMapping("/setting")
    public Map<String, Object> GetPageSettingParams(@RequestParam("teamId") Integer teamId) {

        List<Link> links = linkService.getLinks(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("links", links);

        return result;
    }


    @ApiOperation(value = "获取服务器页面参数")
    @GetMapping("/servers")
    public Map<String, Object> GetPageServersParams(@RequestParam("teamId") Integer teamId) {

        List<EnvironmentOutput> envs = serverService.getEnvironmentsByTeamId(teamId);
        GetServerOutput servers = serverService.getServerOutputByTeamId(teamId, 0, null, null);

        Map<String, Object> result = new HashMap<>();
        result.put("envs", envs);
        result.put("servers", servers);

        return result;
    }

    @ApiOperation(value = "获取成员页面参数")
    @GetMapping("/members")
    public Map<String, Object> GetPageMembersParams(@RequestParam("teamId") Integer teamId, Integer groupId, Integer projectId) {

        List<MemberRoleOutput> teamRoles = memberService.getMemberRoles(1);
        List<MemberRoleOutput> groupRoles = memberService.getMemberRoles(2);
        List<MemberRoleOutput> projectRoles = memberService.getMemberRoles(3);
        List<MemberOutput> members = memberService.getMemberTeamsByTeamId(teamId);
        List<MemberOutput> group = groupService.getMemberGroupsByGroupId(groupId);
        List<MemberOutput> project = projectService.getMemberProjectsByProjectId(projectId);

        Map<String, Object> result = new HashMap<>();

        result.put("members", members);
        result.put("teamRoles", teamRoles);
        result.put("groupRoles", groupRoles);
        result.put("projectRoles", projectRoles);
        result.put("group", group);
        result.put("project", project);

        return result;
    }

//    @ApiOperation(value = "获取项目版本页面参数")
//    @GetMapping("/tags")
//    public Map<String, Object> GetPageTagsParams(@RequestParam("projectId") Integer projectId) {
//
//        ProjectOutput project = projectService.getProjectById(projectId);
//        List<String> branches = projectService.getProjectBranches(projectId);
//        List<TagOutput> tags = projectService.getProjectTags(projectId);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("project", project);
//        result.put("branches", branches);
//        result.put("tags", tags);
//
//        return result;
//    }
}
