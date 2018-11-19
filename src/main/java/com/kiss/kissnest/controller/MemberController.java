package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.BindGroupProjectsOutput;
import com.kiss.kissnest.service.MemberService;
import com.kiss.kissnest.util.ResultOutputUtil;
import com.kiss.kissnest.validator.MemberValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

import java.util.List;

@RestController
@Api(tags = "Member", description = "成员相关接口")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberValidator memberValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.setValidator(memberValidator);
    }

    @PostMapping("/member/access")
    @ApiOperation(value = "获取成员gitlab的凭证")
    public ResultOutput getMemberAccess(@Validated @RequestBody CreateMemberAccessInput createMemberAccessInput) {

        return memberService.getMemberAccess(createMemberAccessInput);
    }

    @PostMapping("/member/apiToken")
    @ApiOperation(value = "获取成员jenkins的凭证")
    public ResultOutput getMemberApiToken(@Validated @RequestBody CreateMemberAccessInput createMemberAccessInput) {

        return memberService.getMemberApiToken(createMemberAccessInput);
    }

    @GetMapping("/member/validate")
    @ApiOperation(value = "判断成员是否拥有凭证")
    public ResultOutput validateMember () {

       return memberService.validateMember();
    }

    @GetMapping("/member")
    public ResultOutput getMember() {

        return memberService.getMember();
    }

    @GetMapping("/member/team/default")
    public ResultOutput getDefaultTeamId(@RequestParam("accountId") Integer accountId) {

        return memberService.getMemberDefaultTeamId(accountId);
    }

    @GetMapping("/member/teams")
    public ResultOutput getMemberTeamsByAccountId(@RequestParam("accountId") Integer accountId) {

        return memberService.getMemberTeamsByAccountId(accountId);
    }

    @PostMapping("/member/search")
    public ResultOutput getMembersByClientId(@RequestBody MemberClientInput memberClientInput) {

        return memberService.getMembersByClientId(memberClientInput);
    }

    @PostMapping("/member/team")
    public ResultOutput createMemberTeam(@RequestBody CreateMemberTeamInput createMemberTeamInput) {

        return memberService.createMemberTeam(createMemberTeamInput);
    }

    @PostMapping("/member/group")
    public ResultOutput bindMemberGroup(@RequestBody BindMemberGroupInput bindMemberGroupInput) {

        return memberService.createMemberGroup(bindMemberGroupInput);
    }

    @PostMapping("/member/project")
    public ResultOutput bindMemberGroup(@RequestBody BindMemberProjectInput bindMemberProjectInput) {

        return memberService.createMemberProject(bindMemberProjectInput);
    }
}
