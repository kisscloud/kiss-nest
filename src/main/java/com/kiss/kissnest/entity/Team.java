package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Team {

    private Integer id;

    private String name;

    private Integer groupsCount;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
