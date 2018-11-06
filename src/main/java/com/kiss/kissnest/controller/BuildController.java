package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.BuildJobInput;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.service.BuildLogService;
import com.kiss.kissnest.validator.JobValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
public class BuildController {

    @Autowired
    private BuildLogService buildLogService;

    @Autowired
    private JobValidator jobValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(jobValidator);
    }

    @PostMapping("/job")
    public ResultOutput createJob(@Validated @RequestBody CreateJobInput createJobInput) {

        return buildLogService.createJob(createJobInput);
    }

    @PostMapping("/job/build")
    public ResultOutput buildJob(@Validated @RequestBody BuildJobInput buildJobInput) {

        return buildLogService.buildJob(buildJobInput);
    }
}
