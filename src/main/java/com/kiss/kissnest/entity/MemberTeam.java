package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MemberTeam {

    private Integer id;

    private Integer memberId;

    private Integer teamId;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updateAt;
}
