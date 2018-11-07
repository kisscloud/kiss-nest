package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.BuildJobInput;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.service.BuildService;
import com.kiss.kissnest.validator.JobValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "Build", description = "构建部署相关接口")
@RequestMapping("/build")
public class BuildController {

    @Autowired
    private BuildService buildLogService;

    @Autowired
    private JobValidator jobValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(jobValidator);
    }

    @PostMapping("/job")
    @ApiOperation(value = "创建构建任务")
    public ResultOutput createJob(@Validated @RequestBody CreateJobInput createJobInput) {

        return buildLogService.createJob(createJobInput);
    }

    @PostMapping("/job/exec")
    @ApiOperation(value = "执行构建任务")
    public ResultOutput ExecJob(@Validated @RequestBody BuildJobInput buildJobInput) {

        return buildLogService.buildJob(buildJobInput);
    }

    @GetMapping("/job/exist")
    public ResultOutput validateJobExist(@RequestParam("projectId") Integer projectId,@RequestParam("type") Integer type) {

        return buildLogService.validateJobExist(projectId,type);
    }
}
