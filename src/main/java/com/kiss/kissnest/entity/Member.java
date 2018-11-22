package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Member {

    private Integer id;

    private Integer accountId;

    private String name;

    private String username;

    private Integer teamId;

    private String accessToken;

    private String apiToken;

    private Integer groupsCount;

    private Integer projectsCount;

    private Integer needsCount;

    private Integer issuesCount;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
