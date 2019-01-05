package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.BindGroupProjectsInput;
import com.kiss.kissnest.input.CreateGroupInput;
import com.kiss.kissnest.input.UpdateGroupInput;
import com.kiss.kissnest.output.BindGroupProjectsOutput;
import com.kiss.kissnest.output.GroupOutput;
import com.kiss.kissnest.service.GroupProjectService;
import com.kiss.kissnest.service.GroupService;
import com.kiss.kissnest.validator.GroupValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public GroupOutput createGroup(@Validated @RequestBody CreateGroupInput createGroupInput) {

        return groupService.createGroup(createGroupInput);
    }

    @PostMapping("/group/projects")
    @ApiOperation(value = "组织绑定项目")
    public List<BindGroupProjectsOutput> bindGroupProjects(@Validated @RequestBody BindGroupProjectsInput bindGroupProjectsInput) {

        return groupProjectService.bindGroupProjects(bindGroupProjectsInput);
    }

    @DeleteMapping("/group")
    @ApiOperation(value = "删除组织")
    public void deleteGroup(@RequestParam("id") Integer id) {

        groupService.deleteGroup(id);
    }

    @PutMapping("/group")
    @ApiOperation(value = "更新组织")
    public GroupOutput updateGroup(@Validated @RequestBody UpdateGroupInput updateGroupInput) {

        return groupService.updateGroup(updateGroupInput);
    }

    @GetMapping("/group")
    @ApiOperation(value = "获取组织")
    public GroupOutput getGroup(@RequestParam("groupId") Integer groupId) {

        return groupService.getGroupById(groupId);
    }

    @GetMapping("/groups")
    @ApiOperation(value = "获取组织列表")
    public List<GroupOutput> getGroups(@RequestParam("teamId") Integer teamId) {

        return groupService.getGroups(teamId);
    }
}
