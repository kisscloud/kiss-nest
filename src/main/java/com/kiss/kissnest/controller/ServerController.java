package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.EnvironmentInput;
import com.kiss.kissnest.input.CreateServerInput;
import com.kiss.kissnest.input.UpdateServerInput;
import com.kiss.kissnest.service.ServerService;
import com.kiss.kissnest.validator.ServerValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "server", description = "服务器相关接口")
public class ServerController {

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerValidator serverValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(serverValidator);
    }

    @PostMapping("/server/environment")
    @ApiOperation(value = "创建服务器环境")
    public ResultOutput createEnvironment(@Validated @RequestBody EnvironmentInput environmentInput) {

        return serverService.createEnvironment(environmentInput);
    }

    @GetMapping("/server/environments")
    @ApiOperation(value = "获取服务器环境")
    public ResultOutput getEnvironmentByTeamId(@RequestParam("teamId") Integer teamId) {

        return serverService.getEnvironmentsByTeamId(teamId);
    }

    @PostMapping("/server")
    @ApiOperation(value = "创建服务器")
    public ResultOutput createServer(@Validated @RequestBody CreateServerInput createServerInput){

        return serverService.createServer(createServerInput);
    }

    @PutMapping("/server")
    public ResultOutput updateServer(@Validated @RequestBody UpdateServerInput updateServerInput) {

        return serverService.updateServer(updateServerInput);
    }

    @GetMapping("/servers")
    public ResultOutput getServers(@RequestParam("teamId") Integer teamId,@RequestParam("page") Integer page,@RequestParam("size") Integer size,Integer envId) {

        return serverService.getServersByTeamId(teamId,page,size,envId);
    }
}
