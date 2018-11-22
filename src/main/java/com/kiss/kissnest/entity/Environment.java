package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Environment {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer type;

    private Integer serverCount;

    private String saltHost;

    private String saltUser;

    private String saltPassword;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
