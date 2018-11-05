package com.kiss.kissnest.controller;

import com.kiss.kissnest.dao.MemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import output.ResultOutput;

@RestController
public class MemberController {

    @Autowired
    private MemberDao memberDao;

//    public ResultOutput createMember() {
//
//
//    }
}
