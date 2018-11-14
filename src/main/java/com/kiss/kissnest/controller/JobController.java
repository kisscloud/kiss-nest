package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.*;
import com.kiss.kissnest.service.BuildService;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import com.kiss.kissnest.validator.JobValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

import java.io.IOException;

@RestController
@Api(tags = "Build", description = "构建部署相关接口")
public class JobController {

    @Autowired
    private BuildService buildLogService;

    @Autowired
    private JobValidator jobValidator;

    @Autowired
    private JenkinsUtil jenkinsUtil;
    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(jobValidator);
    }

    @PostMapping("/job/build")
    @ApiOperation(value = "创建构建任务")
    public ResultOutput createBuildJob(@Validated @RequestBody CreateJobInput createJobInput) {

        return buildLogService.createBuildJob(createJobInput);
    }

    @PostMapping("/job/build/exec")
    @ApiOperation(value = "执行构建任务")
    public ResultOutput execBuildJob(@Validated @RequestBody BuildJobInput buildJobInput) {

        return buildLogService.buildJob(buildJobInput);
    }

    @GetMapping("/job/exist")
    public ResultOutput validateJobExist(@RequestParam("projectId") Integer projectId, @RequestParam("type") Integer type) {

        return buildLogService.validateJobExist(projectId, type);
    }

    @PostMapping("/job/build/logs")
    public ResultOutput getBuildLogs(@Validated @RequestBody BuildLogsInput buildLogsInput) {

        return buildLogService.getBuildLogsByTeamId(buildLogsInput);
    }

    @PostMapping("/job/deploy")
    public ResultOutput createDeployJob(@RequestBody CreateDeployInput createDeployInput) {

        return buildLogService.createDeployJob(createDeployInput);
    }

    @PostMapping("/job/deploy/exec")
    public ResultOutput execDeployJob(@RequestBody DeployJobInput deployJobInput) {

        return buildLogService.deployJob(deployJobInput);
    }
}