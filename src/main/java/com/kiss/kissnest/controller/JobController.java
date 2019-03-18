package com.kiss.kissnest.controller;

import com.kiss.foundation.utils.BeanCopyUtil;
import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.enums.WebSocketMessageTypeEnums;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.*;
import com.kiss.kissnest.service.JobService;
import com.kiss.kissnest.service.PackageRepositoryService;
import com.kiss.kissnest.service.WebSocketService;
import com.kiss.kissnest.util.LangUtil;
import com.kiss.kissnest.util.OutputUtil;
import com.kiss.kissnest.validator.JobValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Api(tags = "Job", description = "构建部署任务相关接口")
@Slf4j
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private JobValidator jobValidator;

    @Autowired
    private PackageRepositoryService packageRepositoryService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OutputUtil outputUtil;

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
    public BuildLogOutput execBuildJob(@Validated @RequestBody BuildJobInput buildJobInput) {

        BuildLog buildLog = jobService.buildJob(buildJobInput);

        BuildLogOutput buildLogOutput = outputUtil.toBuildLogOutput(buildLog);

        webSocketService.sendMessage(WebSocketMessageTypeEnums.BUILD_PROJECT_PENDING.value(), buildLogOutput);

        return buildLogOutput;
    }

    @GetMapping("/job/exist")
    @ApiOperation(value = "检查任务是否存在")
    public Map<String, Boolean> validateJobExist(@RequestParam("projectId") Integer projectId, @RequestParam("type") Integer type) {

        return jobService.validateJobExist(projectId, type);
    }

    @PostMapping("/job/build/logs")
    @ApiOperation(value = "获取构建日志列表")
    public GetBuildLogOutput getBuildLogs(@Validated @RequestBody BuildLogInput buildLogsInput) {

        return jobService.getBuildLogsByTeamId(buildLogsInput);
    }

    @GetMapping("/job/build/log/output")
    @ApiOperation(value = "获取构建日志输出")
    public Map<String, Object> getBuildLogOutputById(@RequestParam("id") Integer id) {

        return jobService.getDeployLogOutputTextById(id);
    }

    @GetMapping("/job/build/result")
    @ApiOperation(value = "获取构建结果")
    public BuildLogOutput getBuildRecentLog(@RequestParam("id") Integer id) {

        return jobService.getBuildRecentLog(id);
    }

    @PostMapping("/job/deploy")
    @ApiOperation(value = "创建部署任务")
    public JobOutput createDeployJob(@RequestBody CreateDeployInput createDeployInput) {

        return jobService.createDeployJob(createDeployInput);
    }

    @PutMapping("/job/deploy")
    @ApiOperation(value = "更新部署任务")
    public JobOutput updateDeployJob(@RequestBody UpdateDeployInput updateDeployInput) {

        return jobService.updateDeployJob(updateDeployInput);
    }

    @PostMapping("/job/deploy/exec")
    @ApiOperation(value = "执行部署任务")
    public DeployLogOutput execDeployJob(@RequestBody DeployJobInput deployJobInput) throws IOException {

        return jobService.deployJob(deployJobInput);
    }

    @GetMapping("/job/deploy/envs")
    @ApiOperation(value = "获取项目可部署环境列表")
    public List<EnvironmentOutput> getDeployEnvs(@RequestParam("projectId") Integer projectId) {

        return jobService.getDeployEnvs(projectId);
    }

    @PostMapping("/job/deploy/logs")
    @ApiOperation(value = "获取部署任务列表")
    public GetDeployLogOutput getDeployLogs(@Validated @RequestBody DeployLogInput deployLogInput) {

        return jobService.getDeployLogs(deployLogInput);
    }

    @GetMapping("/job/deploy/log/output")
    @ApiOperation(value = "获取部署任务日志输出")
    public Map<String, Object> getDeployLogOutputText(@RequestParam("id") Integer id) {

        return jobService.getDeployLogOutputText(id);
    }

    @GetMapping("/jobs")
    @ApiOperation(value = "获取团队项目列表")
    public List<JobOutput> getJobsByTeamId(@RequestParam("teamId") Integer teamId, @RequestParam("type") Integer type) {

        return jobService.getJobsByTeamId(teamId, type);
    }

    @PutMapping("/job/build")
    @ApiOperation(value = "更新构建任务")
    public JobOutput updateBuildJob(@Validated @RequestBody UpdateJobInput updateJobInput) {

        return jobService.updateBuildJob(updateJobInput);
    }

    @GetMapping("/job/package/repository/branches")
    @ApiOperation(value = "获取项目仓库分支")
    public List<String> getPackageRepositoryBranches(@RequestParam("projectId") Integer projectId) {

        return packageRepositoryService.getPackageRepositoryBranches(projectId);
    }

    @GetMapping("/job/package/repository/tags")
    @ApiOperation(value = "获取项目仓库标签")
    public List<String> getPackageRepositoryTags(@RequestParam("projectId") Integer projectId) {

        return packageRepositoryService.getPackageRepositoryTags(projectId);
    }

    @GetMapping("/job/deploy/conf")
    @ApiOperation(value = "获取部署任务进程守护配置模板")
    public Map<String, Object> getProjectDeployConf(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.getProjectDeployConf(projectId, envId);
    }

    @GetMapping("/job/deploy/script")
    @ApiOperation(value = "获取部署任务脚本模板")
    public Map<String, Object> getProjectDeployScript(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.getProjectDeployScript(projectId, envId);
    }

    @GetMapping("/job/pending/count")
    @ApiOperation(value = "正在执行的项目统计")
    public Map<String, Integer> getPendingJobCount() {

        return jobService.getPendingJobCount();
    }

    @GetMapping("/job/build")
    @ApiOperation(value = "获取项目构建任务")
    public JobOutput getBuildJobByProjectId(@RequestParam("projectId") Integer projectId) {

        Job job = jobService.getBuildJobByProjectId(projectId);

        if (job == null) {
            return null;
        }

        return BeanCopyUtil.copy(job, JobOutput.class, BeanCopyUtil.defaultFieldNames);
    }

    @GetMapping("/job/deploy")
    @ApiOperation(value = "获取项目部署任务")
    public JobOutput getDeployJobByProjectId(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        Job job = jobService.getDeployJobByProjectId(projectId, envId);

        if (job == null) {
            return null;
        }

        return BeanCopyUtil.copy(job, JobOutput.class, BeanCopyUtil.defaultFieldNames);
    }

    @GetMapping("/job/program/check")
    @ApiOperation(value = "检查程序状态")
    public HashMap getCheckProgram(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.checkProgram(projectId, envId);
    }

    @GetMapping("/job/program/stop")
    @ApiOperation(value = "停止程序")
    public HashMap getStopProgram(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.stopProgram(projectId, envId);
    }

    @GetMapping("/job/program/start")
    @ApiOperation(value = "启动程序")
    public HashMap getStartProgram(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.startProgram(projectId, envId);
    }

    @GetMapping("/job/program/restart")
    @ApiOperation(value = "重启程序")
    public HashMap getRestartProgram(@RequestParam("projectId") Integer projectId, @RequestParam("envId") Integer envId) {

        return jobService.restartProgram(projectId, envId);
    }

    @PostMapping("/job/jenkins/notification")
    @ApiOperation(value = "Jenkins消息回调")
    public void postJenkinsNotification(@RequestBody JenkinsNotification data) {
        log.info("Jenkins 回调数据：{}", data);
        if (data.isQueued()) {
            jobService.buildJobQueued(data.getName(), data.getBuild().getQueue_id(), data.getBuild().getNumber(), data.getUrl(), data.getBuild().getScm().getBranch(), data.getBuild().getScm().getCommit());
        } else if (data.isStarted()) {
            jobService.buildJobStarted(data.getName(), data.getBuild().getQueue_id());
        } else if (data.isFinalized()) {
            jobService.buildJobFinalized(data.getName(), data.getBuild().getQueue_id(), data.getBuild().getStatus(), data.getUrl());
        }
    }
}
