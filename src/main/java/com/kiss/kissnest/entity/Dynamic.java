package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Dynamic {

    private Integer id;

    private Integer teamId;

    private Integer groupId;

    private Integer projectId;

    private Integer targetType;

    private String log;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
