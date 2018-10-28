package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectMember {

    private Integer id;

    private Integer projectId;

    private Integer memberId;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
