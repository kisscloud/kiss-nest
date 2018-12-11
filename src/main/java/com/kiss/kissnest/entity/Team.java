package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Team {

    private Integer id;

    private String name;

    private String slug;

    private Integer repositoryId;

    private Integer groupsCount;

    private Integer membersCount;

    private Integer projectsCount;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
