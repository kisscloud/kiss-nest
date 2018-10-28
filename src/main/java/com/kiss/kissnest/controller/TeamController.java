package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.input.CreateTeamInput;
import com.kiss.kissnest.input.UpdateTeamInput;
import com.kiss.kissnest.service.TeamService;
import com.kiss.kissnest.util.BeanCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "Team", description = "团队相关接口")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/team")
    @ApiOperation(value = "添加团队")
    public ResultOutput createTeam (@RequestBody CreateTeamInput teamInput) {

        return teamService.createTeam((Team) BeanCopyUtil.copy(teamInput,Team.class));
    }

    @DeleteMapping("/team")
    @ApiOperation(value = "删除团队")
    public ResultOutput deleteTeam (@RequestParam("id") Integer id) {

        return teamService.deleteTeamById(id);
    }

    @PutMapping("/team")
    @ApiOperation(value = "更新团队")
    public ResultOutput updateTeam (@RequestBody UpdateTeamInput teamInput) {

        return teamService.updateTeam((Team) BeanCopyUtil.copy(teamInput,Team.class));
    }

    @GetMapping("/teams")
    @ApiOperation(value = "获取团队")
    public ResultOutput getTeams () {

        return teamService.getTeams();
    }
}
