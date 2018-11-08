package com.kiss.kissnest.controller;

import com.kiss.kissnest.service.BuildService;
import com.kiss.kissnest.service.GroupService;
import com.kiss.kissnest.service.ProjectService;
import com.kiss.kissnest.util.ResultOutputUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private BuildService buildLogService;

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
    public ResultOutput GetPageProjectsParams(Integer teamId) {

        ResultOutput projects = projectService.getProjects(teamId);
        ResultOutput groups = groupService.getGroups(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());
        result.put("groups", groups.getData());

        return ResultOutputUtil.success(result);
    }


    @ApiOperation(value = "获取构建页面参数")
    @GetMapping("/build")
    public ResultOutput GetPageBuildParams(Integer teamId) {

        ResultOutput projects = projectService.getProjects(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());

        return ResultOutputUtil.success(result);
    }

    @ApiOperation(value = "获取仓库页面参数")
    @GetMapping("/repositories")
    public ResultOutput GetPageRepositoriesParams(Integer teamId) {

        ResultOutput projects = projectService.getProjects(teamId);

        Map<String, Object> result = new HashMap<>();
        result.put("projects", projects.getData());

        return ResultOutputUtil.success(result);
    }

}
