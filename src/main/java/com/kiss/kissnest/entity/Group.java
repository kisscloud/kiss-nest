package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Group {

    private Integer id;

    private String name;

    private Integer teamId;

    private String slug;

    private Integer projectsCount;

    private Integer membersCount;

    private Integer needsCount;

    private Integer needSolvedCount;

    private Integer issuesCount;

    private Integer issueSolvedCount;

    private Integer operatorId;

    private String operatorName;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;
}
