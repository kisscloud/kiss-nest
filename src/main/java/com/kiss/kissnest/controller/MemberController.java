package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.CreateMemberAccessInput;
import com.kiss.kissnest.service.MemberService;
import com.kiss.kissnest.validator.MemberValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import output.ResultOutput;

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
}
