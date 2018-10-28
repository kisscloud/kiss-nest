package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.CreateProjectInput;
import com.kiss.kissnest.input.UpdateProjectInput;
import com.kiss.kissnest.service.ProjectService;
import com.kiss.kissnest.util.BeanCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "Project", description = "项目相关接口")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/project")
    @ApiOperation(value = "添加项目")
    public ResultOutput createProject (@RequestBody CreateProjectInput createProjectInput) {

        return projectService.createProject((Project) BeanCopyUtil.copy(createProjectInput,Project.class));
    }

    @DeleteMapping("/project")
    @ApiOperation(value = "删除项目")
    public ResultOutput deleteProject (@RequestParam("id") Integer id) {

        return projectService.deleteProject(id);
    }

    @PutMapping("/project")
    @ApiOperation(value = "更新项目")
    public ResultOutput updateProject (@RequestBody UpdateProjectInput updateProjectInput) {

        return projectService.updateProject((Project) BeanCopyUtil.copy(updateProjectInput,Project.class));
    }

    @GetMapping("/projects")
    @ApiOperation(value = "获取项目")
    public ResultOutput getProjects () {

        return projectService.getProjects();
    }
}
