package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.CreateTeamInput;
import com.kiss.kissnest.input.UpdateTeamInput;
import com.kiss.kissnest.output.TeamOutput;
import com.kiss.kissnest.service.TeamService;
import com.kiss.kissnest.validator.TeamValidaor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags = "Team", description = "团队相关接口")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamValidaor teamValidaor;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(teamValidaor);
    }

    @PostMapping("/team")
    @ApiOperation(value = "添加团队")
    public TeamOutput createTeam(@Validated @RequestBody CreateTeamInput teamInput) {

        return teamService.createTeam(teamInput);
    }

    @DeleteMapping("/team")
    @ApiOperation(value = "删除团队")
    public void deleteTeam(@RequestParam("id") Integer id) {
        teamService.deleteTeamById(id);
    }

    @PutMapping("/team")
    @ApiOperation(value = "更新团队")
    public TeamOutput updateTeam(@Validated @RequestBody UpdateTeamInput updateTeamInput) {

        return teamService.updateTeam(updateTeamInput);
    }

    @GetMapping("/team/change")
    @ApiOperation(value = "切换团队")
    public void changeTeam(@RequestParam("teamId") Integer teamId) {
        teamService.changeTeam(teamId);
    }
}
