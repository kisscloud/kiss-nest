package com.kiss.kissnest.controller;

import com.kiss.kissnest.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
@Api(tags = "operation", description = "操作记录相关接口")
public class OperationController {

    @Autowired
    private OperationLogService operationLogService;
    @GetMapping("/operation/dynamics")
    @ApiOperation(value = "获取动态记录")
    public ResultOutput getDynamics (@RequestParam("teamId") Integer teamId,Integer groupId,Integer projectId) {

        return operationLogService.getDynamics(teamId,groupId,projectId);
    }
}
