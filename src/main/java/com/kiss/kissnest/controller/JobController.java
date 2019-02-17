package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.*;
import com.kiss.kissnest.service.JobService;
import com.kiss.kissnest.service.PackageRepositoryService;
import com.kiss.kissnest.validator.JobValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@Api(tags = "Job", description = "构建部署任务相关接口")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobValidator jobValidator;

    @Autowired
    private PackageRepositoryService packageRepositoryService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(jobValidator);
    }

    @PostMapping("/job/build")
    @ApiOperation(value = "创建构建任务")
    public JobOutput createBuildJob(@Validated @RequestBody CreateJobInput createJobInput) {

        return jobService.createBuildJob(createJobInput);
    }

    @PostMapping("/job/build/exec")
    @ApiOperation(value = "执行构建任务")
    public Map<String, Object> execBuildJob(@Validated @RequestBody BuildJobInput buildJobInput) {

        return jobService.buildJob(buildJobInput);
    }

    @GetMapping("/job/exist")
    public Map<String, Boolean> validateJobExist(@RequestParam("projectId") Integer projectId, @RequestParam("type") Integer type) {

        return jobService.validateJobExist(projectId, type);
    }

    @PostMapping("/job/build/logs")
    public GetBuildLogOutput getBuildLogs(@Validated @RequestBody BuildLogInput buildLogsInput) {

        return jobService.getBuildLogsByTeamId(buildLogsInput);
    }

    @GetMapping("/job/build/log/output")
    public Map<String, Object> getBuildLogOutputById(@RequestParam("id") Integer id) {

        return jobService.getDeployLogOutputTextById(id);
    }

    @GetMapping("/job/build/result")
    public BuildLogOutput getBuildRecentLog(@RequestParam("id") Integer id) {

        return jobService.getBuildRecentLog(id);
    }

    @PostMapping("/job/deploy")
    public JobOutput createDeployJob(@RequestBody CreateDeployInput createDeployInput) {

        return jobService.createDeployJob(createDeployInput);
    }

    @PutMapping("/job/deploy")
    public JobOutput updateDeployJob(@RequestBody UpdateDeployInput updateDeployInput) {

        return jobService.updateDeployJob(updateDeployInput);
    }

    @PostMapping("/job/deploy/exec")
    public DeployLogOutput execDeployJob(@RequestBody DeployJobInput deployJobInput) throws IOException {

        return jobService.deployJob(deployJobInput);
    }

    @GetMapping("/job/deploy/envs")
    public List<EnvironmentOutput> getDeployEnvs(@RequestParam("projectId") Integer projectId) {

        return jobService.getDeployEnvs(projectId);
    }

    @PostMapping("/job/deploy/logs")
    public GetDeployLogOutput getDeployLogs(@Validated @RequestBody DeployLogInput deployLogInput) {

        return jobService.getDeployLogs(deployLogInput);
    }

    @GetMapping("/job/deploy/log/output")
    public Map<String, Object> getDeployLogOutputText(@RequestParam("id") Integer id) {

        return jobService.getDeployLogOutputText(id);
    }

    @GetMapping("/jobs")
    public List<JobOutput> getJobsByTeamId(@RequestParam("teamId") Integer teamId, @RequestParam("type") Integer type) {

        return jobService.getJobsByTeamId(teamId, type);
    }

    @PutMapping("/job/build")
    public JobOutput updateBuildJob(@Validated @RequestBody UpdateJobInput updateJobInput) {

        return jobService.updateBuildJob(updateJobInput);
    }

    @GetMapping("/job/package/repository/branches")
    public List<String> getPackageRepositoryBranches(@RequestParam("projectId") Integer projectId) {

        return packageRepositoryService.getPackageRepositoryBranches(projectId);
    }

    @GetMapping("/job/package/repository/tags")
    public List<String> getPackageRepositoryTags(@RequestParam("projectId") Integer projectId) {

        return packageRepositoryService.getPackageRepositoryTags(projectId);
    }

    @GetMapping("/job/deploy/conf")
    public Map<String, Object> getProjectDeployConf(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.getProjectDeployConf(projectId, envId);
    }

    @GetMapping("/job/deploy/script")
    public Map<String, Object> getProjectDeployScript(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.getProjectDeployScript(projectId, envId);
    }

    @GetMapping("/job/pending/count")
    public Map<String,Integer> getPendingJobCount() {

        return jobService.getPendingJobCount();
    }
}
