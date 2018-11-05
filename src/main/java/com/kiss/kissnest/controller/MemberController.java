package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.CreateMemberAccessInput;
import com.kiss.kissnest.service.MemberService;
import com.kiss.kissnest.validator.MemberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
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
    public ResultOutput getMemberAccess(@Validated @RequestBody CreateMemberAccessInput createMemberAccessInput) {

        return memberService.getMemberAccess(createMemberAccessInput);
    }
}
