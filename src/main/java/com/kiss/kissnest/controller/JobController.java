package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.*;
import com.kiss.kissnest.service.BuildService;
import com.kiss.kissnest.service.PackageRepositoryService;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.validator.JobValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "Job", description = "构建部署任务相关接口")
public class JobController {

    @Autowired
    private BuildService buildService;

    @Autowired
    private JobValidator jobValidator;

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @Autowired
    private PackageRepositoryService packageRepositoryService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(jobValidator);
    }

    @PostMapping("/job/build")
    @ApiOperation(value = "创建构建任务")
    public ResultOutput createBuildJob(@Validated @RequestBody CreateJobInput createJobInput) {

        return buildService.createBuildJob(createJobInput);
    }

    @PostMapping("/job/build/exec")
    @ApiOperation(value = "执行构建任务")
    public ResultOutput execBuildJob(@Validated @RequestBody BuildJobInput buildJobInput) {

        return buildService.buildJob(buildJobInput);
    }

    @GetMapping("/job/exist")
    public ResultOutput validateJobExist(@RequestParam("projectId") Integer projectId, @RequestParam("type") Integer type) {

        return buildService.validateJobExist(projectId, type);
    }

    @PostMapping("/job/build/logs")
    public ResultOutput getBuildLogs(@Validated @RequestBody BuildLogsInput buildLogsInput) {

        return buildService.getBuildLogsByTeamId(buildLogsInput);
    }

    @GetMapping("/job/build/result")
    public ResultOutput getBuildRecentLog(@RequestParam("id") Integer id) {

        return buildService.getBuildRecentLog(id);
    }

    @PostMapping("/job/deploy")
    public ResultOutput createDeployJob(@RequestBody CreateDeployInput createDeployInput) {

        return buildService.createDeployJob(createDeployInput);
    }

    @PutMapping("/job/deploy")
    public ResultOutput updateDeployJob(@RequestBody UpdateDeployInput updateDeployInput) {

        return buildService.updateDeployJob(updateDeployInput);
    }

    @PostMapping("/job/deploy/exec")
    public ResultOutput execDeployJob(@RequestBody DeployJobInput deployJobInput) {

        return buildService.deployJob(deployJobInput);
    }

    @GetMapping("/jobs")
    public ResultOutput getJobsByTeamId(@RequestParam("teamId") Integer teamId,@RequestParam("type") Integer type) {

        return buildService.getJobsByTeamId(teamId,type);
    }

    @PutMapping("/job/build")
    public ResultOutput updateBuildJob(@Validated @RequestBody UpdateJobInput updateJobInput) {

        return buildService.updateBuildJob(updateJobInput);
    }

    @GetMapping("/job/package/repository/branches")
    public ResultOutput getPackageRepositoryBranches(@RequestParam("projectId") Integer projectId) {

        return packageRepositoryService.getPackageRepositoryBranches(projectId);
    }

    @GetMapping("/job/package/repository/tags")
    public ResultOutput getPackageRepositoryTags(@RequestParam("projectId") Integer projectId) {

        return packageRepositoryService.getPackageRepositoryTags(projectId);
    }

    @GetMapping("/job/deploy/conf")
    public ResultOutput getProjectDeployConf(@RequestParam("projectId") Integer projectId,@RequestParam("envId") Integer envId) {

        return buildService.getProjectDeployConf(projectId,envId);
    }
}
