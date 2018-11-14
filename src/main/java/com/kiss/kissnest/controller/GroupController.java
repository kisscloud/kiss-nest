package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.GroupProject;
import com.kiss.kissnest.entity.TeamGroup;
import com.kiss.kissnest.input.BindGroupProjectsInput;
import com.kiss.kissnest.input.CreateGroupInput;
import com.kiss.kissnest.input.UpdateGroupInput;
import com.kiss.kissnest.service.GroupProjectService;
import com.kiss.kissnest.service.GroupService;
import com.kiss.kissnest.service.TeamGroupService;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.validator.GroupValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "Group", description = "组织相关接口")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupProjectService groupProjectService;

    @Autowired
    private GroupValidator groupValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(groupValidator);
    }

    @PostMapping("/group")
    @ApiOperation(value = "添加组织")
    public ResultOutput createGroup (@Validated @RequestBody CreateGroupInput createGroupInput) {

        ResultOutput createOutput = groupService.createGroup(createGroupInput);

        return createOutput;
    }

    @PostMapping("/group/projects")
    @ApiOperation(value = "组织绑定项目")
    public ResultOutput bindGroupProjects (@Validated @RequestBody BindGroupProjectsInput bindGroupProjectsInput) {

        return groupProjectService.bindGroupProjects(bindGroupProjectsInput);
    }

    @DeleteMapping("/group")
    @ApiOperation(value = "删除组织")
    public ResultOutput deleteGroup (@RequestParam("id") Integer id) {

        return groupService.deleteGroup(id);
    }

    @PutMapping("/group")
    @ApiOperation(value = "更新组织")
    public ResultOutput updateGroup (@Validated @RequestBody UpdateGroupInput updateGroupInput) {

        return groupService.updateGroup(updateGroupInput);
    }

    @GetMapping("/groups")
    @ApiOperation(value = "获取组织")
    public ResultOutput getGroups (@RequestParam("teamId") Integer teamId) {

        return groupService.getGroups(teamId);
    }
}
