package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MemberProject {

    private Integer id;

    private Integer teamId;

    private Integer projectId;

    private Integer memberId;

    private Integer role;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
