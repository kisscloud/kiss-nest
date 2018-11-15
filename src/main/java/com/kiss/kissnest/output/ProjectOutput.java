package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectOutput {

    private Integer id;

    private String name;

    private Integer teamId;

    private Integer groupId;

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
