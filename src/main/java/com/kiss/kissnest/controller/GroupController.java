package com.kiss.kissnest.controller;

import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.TeamGroup;
import com.kiss.kissnest.input.CreateGroupInput;
import com.kiss.kissnest.input.UpdateGroupInput;
import com.kiss.kissnest.service.GroupService;
import com.kiss.kissnest.service.TeamGroupService;
import com.kiss.kissnest.util.BeanCopyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

@RestController
@Api(tags = "Group", description = "组织相关接口")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private TeamGroupService teamGroupService;


    @PostMapping("/group")
    @ApiOperation(value = "添加组织")
    public ResultOutput createGroup (@RequestBody CreateGroupInput createGroupInput) {

        ResultOutput createOutput = groupService.createGroup((Group) BeanCopyUtil.copy(createGroupInput,Group.class));

        if (createOutput.getCode() != 200) {
            return createOutput;
        }

        ResultOutput createTeamGroup = teamGroupService.createTeamGroup((TeamGroup) BeanCopyUtil.copy(createGroupInput,TeamGroup.class));

        return createOutput;
    }

    @DeleteMapping("/group")
    @ApiOperation(value = "删除组织")
    public ResultOutput deleteGroup (Integer id) {

        return groupService.deleteGroup(id);
    }

    @PutMapping("/group")
    @ApiOperation(value = "更新组织")
    public ResultOutput updateGroup (@RequestBody UpdateGroupInput updateGroupInput) {

        return groupService.updateGroup((Group) BeanCopyUtil.copy(updateGroupInput,Group.class));
    }

    @GetMapping("/groups")
    @ApiOperation(value = "获取组织")
    public ResultOutput getGroups () {

        return groupService.getGroups();
    }
}
