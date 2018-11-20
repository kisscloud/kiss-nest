package com.kiss.kissnest.controller;

import com.kiss.kissnest.service.*;
import com.kiss.kissnest.util.ResultOutputUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

import java.util.HashMap;
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
    private BuildService buildService;

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
    public ResultOutput GetPageGroupsParams(Integer teamId) {

        ResultOutput groups = groupService.getGroups(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("groups", groups.getData());
        result.put("status", groups.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取项目组页面参数")
    @GetMapping("/projects")
    public ResultOutput GetPageProjectsParams(@RequestParam("teamId") Integer teamId, Integer groupId) {

        ResultOutput projects = projectService.getProjects(teamId, groupId);
        ResultOutput groups = groupService.getGroups(teamId);
        ResultOutput types = projectService.getProjectTypes();

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());
        result.put("groups", groups.getData());
        result.put("types", types.getData());

        return ResultOutputUtil.success(result);
    }


    @ApiOperation(value = "获取构建记录页面参数")
    @GetMapping("/build/logs")
    public ResultOutput GetPageBuildLogsParams(Integer teamId) {

        ResultOutput projects = projectService.getProjectsWithoutBuildJob(teamId);
        ResultOutput buildProjects = projectService.getBuildProjects(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());
        result.put("build", buildProjects.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取构建任务页面参数")
    @GetMapping("/build/jobs")
    public ResultOutput GetPageBuildJobsParams(Integer teamId) {

        ResultOutput jobs = buildService.getJobsByTeamId(teamId, 1);
        ResultOutput projects = projectService.getProjectsWithoutBuildJob(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs.getData());
        result.put("projects", projects.getData());

        return ResultOutputUtil.success(result);
    }


    @ApiOperation(value = "获取仓库页面参数")
    @GetMapping("/repositories")
    public ResultOutput GetPageRepositoriesParams(@RequestParam("teamId") Integer teamId, Integer groupId) {

        ResultOutput projects = projectService.getProjects(teamId, groupId);
        ResultOutput repositories = projectRepositoryService.getProjectRepositoriesByTeamId(teamId);
        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());
        result.put("repositories", repositories.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取部署记录页面参数")
    @GetMapping("/deploy/logs")
    public ResultOutput GetPageDeployLogsParams(Integer teamId) {

        ResultOutput projects = projectService.getProjectsWithoutBuildJob(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取部署任务页面参数")
    @GetMapping("/deploy/jobs")
    public ResultOutput GetPageDeployJobsParams(Integer teamId) {

        ResultOutput jobs = buildService.getJobsByTeamId(teamId, 2);
        ResultOutput envs = serverService.getEnvironmentsByTeamId(teamId);
        ResultOutput projects = projectService.getProjects(teamId, null);

        Map<String, Object> result = new HashMap<>();
        result.put("jobs", jobs.getData());
        result.put("envs", envs.getData());
        result.put("projects", projects.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取设置页面参数")
    @GetMapping("/setting")
    public ResultOutput GetPageSettingParams(@RequestParam("teamId") Integer teamId) {

        ResultOutput links = linkService.getLinks(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("links", links.getData());

        return ResultOutputUtil.success(result);
    }


    @ApiOperation(value = "获取服务器页面参数")
    @GetMapping("/servers")
    public ResultOutput GetPageServersParams(@RequestParam("teamId") Integer teamId) {

        ResultOutput envs = serverService.getEnvironmentsByTeamId(teamId);
        ResultOutput servers = serverService.getServerOutputByTeamId(teamId, 0, null, null);

        Map<String, Object> result = new HashMap<>();
        result.put("envs", envs.getData());
        result.put("servers", servers.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取成员页面参数")
    @GetMapping("/members")
    public ResultOutput GetPageMembersParams(@RequestParam("teamId") Integer teamId) {

        ResultOutput roles = memberService.getMemberRoles(1);

        Map<String, Object> result = new HashMap<>();
        result.put("roles", roles.getData());

        return ResultOutputUtil.success(result);
    }
}
