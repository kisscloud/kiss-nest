package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.CreateEnvironmentInput;
import com.kiss.kissnest.input.CreateServerInput;
import com.kiss.kissnest.input.UpdateEnvironmentInput;
import com.kiss.kissnest.input.UpdateServerInput;
import com.kiss.kissnest.output.EnvironmentOutput;
import com.kiss.kissnest.output.GetServerOutput;
import com.kiss.kissnest.output.ServerOutput;
import com.kiss.kissnest.service.ServerService;
import com.kiss.kissnest.validator.ServerValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(tags = "Server", description = "服务器相关接口")
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
    public EnvironmentOutput createEnvironment(@Validated @RequestBody CreateEnvironmentInput environmentInput) {

        return serverService.createEnvironment(environmentInput);
    }


    @PutMapping("/server/environment")
    public EnvironmentOutput  updateEnvironment(@Validated @RequestBody UpdateEnvironmentInput updateEnvironmentInput) {

        return serverService.updateEnvironment(updateEnvironmentInput);
    }

    @DeleteMapping("/server/environment")
    public void deleteEnvironment(@RequestParam("id") Integer id) {

         serverService.deleteEnvironmentById(id);
    }

    @GetMapping("/server/environments")
    @ApiOperation(value = "获取服务器环境")
    public List<EnvironmentOutput> getEnvironmentByTeamId(@RequestParam("teamId") Integer teamId) {

        return serverService.getEnvironmentsByTeamId(teamId);
    }

    @PostMapping("/server")
    @ApiOperation(value = "创建服务器")
    public ServerOutput createServer(@Validated @RequestBody CreateServerInput createServerInput){

        return serverService.createServer(createServerInput);
    }

    @PutMapping("/server")
    public ServerOutput  updateServer(@Validated @RequestBody UpdateServerInput updateServerInput) {

        return serverService.updateServer(updateServerInput);
    }

    @GetMapping("/servers")
    public GetServerOutput getServers(@RequestParam("teamId") Integer teamId, @RequestParam("page") Integer page, @RequestParam("size") Integer size, Integer envId) {

        return serverService.getServerOutputByTeamId(teamId,page,size,envId);
    }

    @DeleteMapping("/server")
    public void deleteServers(@RequestParam("id") Integer id) {
         serverService.deleteServerById(id);
    }
}
