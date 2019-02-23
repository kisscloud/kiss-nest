package com.kiss.kissnest.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Project implements Serializable {

    private Integer id;

    private String name;

    private Integer teamId;

    private Integer groupId;

    private Integer repositoryId;

    private Integer membersCount;

    private Integer needsCount;

    private Integer issuesCount;

    private Integer operatorId;

    private String operatorName;

    private Integer type;

    private String slug;

    private Date createdAt;

    private Date updatedAt;
}
