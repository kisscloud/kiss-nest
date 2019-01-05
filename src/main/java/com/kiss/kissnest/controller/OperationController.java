package com.kiss.kissnest.controller;

import com.kiss.kissnest.output.GetDynamicOutput;
import com.kiss.kissnest.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Api(tags = "operation", description = "操作记录相关接口")
public class OperationController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/operation/dynamics")
    @ApiOperation(value = "获取动态记录")
    public GetDynamicOutput getDynamics(@RequestParam("teamId") Integer teamId, @RequestParam("page") Integer page, @RequestParam("size") Integer size, Integer groupId, Integer projectId) {

        return operationLogService.getDynamics(teamId, page, size, groupId, projectId);
    }
}
