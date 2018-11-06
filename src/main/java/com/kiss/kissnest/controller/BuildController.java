package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.BuildJobInput;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.service.BuildLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
public class BuildController {

    @Autowired
    private BuildLogService buildLogService;

    @PostMapping("/job")
    public ResultOutput createJob(@RequestBody CreateJobInput createJobInput) {

        return buildLogService.createJob(createJobInput);
    }

    @PostMapping("/job/build")
    public ResultOutput buildJob(@RequestBody BuildJobInput buildJobInput) {

        return buildLogService.buildJob(buildJobInput);
    }
}
