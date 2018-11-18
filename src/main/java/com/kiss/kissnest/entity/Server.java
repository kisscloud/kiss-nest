package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Server {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer envId;

    private String innerIp;

    private String outerIp;

    private String parameters;

    private Integer runTime;

    private Integer status;

    private Integer operatorId;

    private String operatorName;

    private Date lastDeployedAt;

    private Date createdAt;

    private Date updatedAt;
}

