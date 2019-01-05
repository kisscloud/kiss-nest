package com.kiss.kissnest.controller;

import com.kiss.account.output.ClientAccountOutput;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.MemberOutput;
import com.kiss.kissnest.output.MemberRoleOutput;
import com.kiss.kissnest.output.TeamOutput;
import com.kiss.kissnest.service.MemberService;
import com.kiss.kissnest.validator.MemberValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


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
    public void getMemberAccess(@Validated @RequestBody CreateMemberAccessInput createMemberAccessInput) {
        memberService.getMemberAccess(createMemberAccessInput);
    }

    @PostMapping("/member/apiToken")
    @ApiOperation(value = "获取成员jenkins的凭证")
    public void getMemberApiToken(@Validated @RequestBody CreateMemberAccessInput createMemberAccessInput) {
        memberService.getMemberApiToken(createMemberAccessInput);
    }

    @GetMapping("/member/validate")
    @ApiOperation(value = "判断成员是否拥有凭证")
    public Map<String, Object> validateMember() {

        return memberService.validateMember();
    }

    @GetMapping("/member")
    public MemberOutput getMember() {

        return memberService.getMember();
    }

    @GetMapping("/member/team/default")
    public Map<String, Object> getDefaultTeamId(@RequestParam("accountId") Integer accountId) {

        return memberService.getMemberDefaultTeamId(accountId);
    }

    @GetMapping("/member/teams")
    public List<TeamOutput> getMemberTeamsByAccountId(@RequestParam("accountId") Integer accountId) {

        return memberService.getMemberTeamsByAccountId(accountId);
    }

    @PostMapping("/member/team/search")
    public List<ClientAccountOutput> getMembersByClientId(@RequestBody MemberClientInput memberClientInput) {

        return memberService.getMembersByClientId(memberClientInput);
    }

    @PostMapping("/member/group/search")
    public List<MemberOutput> getGroupMembers(@Validated @RequestBody GroupMemberSearchInput groupMemberSearchInput) {

        return memberService.getGroupValidMembers(groupMemberSearchInput);
    }

    @PostMapping("/member/project/search")
    public List<MemberOutput> getMembersByClientId(@RequestBody ProjectMemberSearchInput projectMemberSearchInput) {

        return memberService.getProjectValidMembers(projectMemberSearchInput);
    }

    @PostMapping("/member/team")
    public List<MemberOutput> createMemberTeam(@RequestBody CreateMemberTeamInput createMemberTeamInput) {

        return memberService.createMemberTeam(createMemberTeamInput);
    }

    @PostMapping("/member/group")
    public void bindMemberGroup(@Validated @RequestBody BindMemberGroupInput bindMemberGroupInput) {

        memberService.createMemberGroup(bindMemberGroupInput);
    }

    @PostMapping("/member/project")
    public void bindMemberGroup(@Validated @RequestBody BindMemberProjectInput bindMemberProjectInput) {

        memberService.createMemberProject(bindMemberProjectInput);
    }

    @GetMapping("/member/roles")
    public List<MemberRoleOutput> getMemberRoles(@RequestParam("type") Integer type) {

        return memberService.getMemberRoles(type);
    }

    @GetMapping("/members")
    public List<MemberOutput> getMembers(@RequestParam("teamId") Integer teamId, Integer groupId, Integer projectId) {

        return memberService.getMembers(teamId, groupId, projectId);
    }
}
