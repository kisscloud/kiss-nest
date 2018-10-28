package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Server {

    private Integer id;

    private String ip;

    private String parameters;

    private Integer runTime;

    private Integer operatorId;

    private String operatorName;

    private Date lastDeployedAt;

    private Date createdAt;

    private Date updatedAt;
}

