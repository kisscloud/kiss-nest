package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupMember {

    private Integer id;

    private Integer groupId;

    private Integer accountId;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
