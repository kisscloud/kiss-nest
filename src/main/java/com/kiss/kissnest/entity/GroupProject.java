package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupProject {

    private Integer id;

    private Integer groupId;

    private Integer projectId;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
