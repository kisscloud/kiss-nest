package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.CreateProjectInput;
import com.kiss.kissnest.input.CreateProjectRepositoryInput;
import com.kiss.kissnest.input.CreateTagInput;
import com.kiss.kissnest.input.UpdateProjectInput;
import com.kiss.kissnest.service.ProjectRepositoryService;
import com.kiss.kissnest.service.ProjectService;
import com.kiss.kissnest.validator.ProjectValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;
import utils.BeanCopyUtil;

@RestController
@Api(tags = "Project", description = "项目相关接口")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectValidator projectValidator;

    @Autowired
    private ProjectRepositoryService projectRepositoryService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(projectValidator);
    }

    @PostMapping("/project")
    @ApiOperation(value = "添加项目")
    public ResultOutput createProject(@Validated @RequestBody CreateProjectInput createProjectInput) {

        return projectService.createProject(createProjectInput);
    }

    @DeleteMapping("/project")
    @ApiOperation(value = "删除项目")
    public ResultOutput deleteProject(@RequestParam("id") Integer id) {

        return projectService.deleteProject(id);
    }

    @PutMapping("/project")
    @ApiOperation(value = "更新项目")
    public ResultOutput updateProject(@Validated @RequestBody UpdateProjectInput updateProjectInput) {

        return projectService.updateProject(updateProjectInput);
    }

    @GetMapping("/projects")
    @ApiOperation(value = "获取项目")
    public ResultOutput getProjects(@RequestParam("teamId") Integer teamId, Integer groupId) {

        return projectService.getProjects(teamId, groupId);
    }

    @PostMapping("/project/repository")
    @ApiOperation(value = "创建项目仓库")
    public ResultOutput createProjectRepository(@Validated @RequestBody CreateProjectRepositoryInput createProjectRepositoryInput) {

        return projectRepositoryService.createProjectRepository(createProjectRepositoryInput);
    }

    @GetMapping("/project/repository")
    @ApiOperation(value = "查询项目仓库详情")
    public ResultOutput getProjectRepository(@RequestParam("projectId") Integer projectId) {

        return projectRepositoryService.getProjectRepositoryByProjectId(projectId);
    }

    @GetMapping("/project/branches")
    @ApiOperation(value = "查询项目的所有分支")
    public ResultOutput getProjectBranches(@RequestParam("projectId") Integer projectId) {

        return projectService.getProjectBranches(projectId);
    }

    @GetMapping("/project/tags")
    @ApiOperation(value = "查询项目的所有版本")
    public ResultOutput getProjectTags(@RequestParam("projectId") Integer projectId) {

        return projectService.getProjectTags(projectId);
    }

    @GetMapping("/project/repository/validate")
    @ApiOperation(value = "校验项目仓库是否存在")
    public ResultOutput validateProjectRepositoryExist(@RequestParam("projectId") Integer projectId) {

        return projectRepositoryService.validateProjectRepositoryExist(projectId);
    }

    @PostMapping("/project/tag")
    @ApiOperation(value = "创建项目版本")
    public ResultOutput createTag(@Validated @RequestBody CreateTagInput createTagInput) {

        return projectService.addTag(createTagInput);
    }

    @GetMapping("/project")
    @ApiOperation(value = "查询项目详情")
    public ResultOutput getProjectById(@RequestParam("id") Integer id) {

        return projectService.getProjectById(id);
    }

}
