package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class ServerOutput {

    private Integer id;

    private String name;

    private Integer envId;

    private String envName;

    private Integer projectId;

    private String projectName;

    private Integer status;

    private String statusText;

    private String innerIp;

    private String outerIp;

    private String parameters;

    private Integer runTime;

    private Integer operatorId;

    private String operatorName;

    private Date lastDeployedAt;

    private Date createdAt;

    private Date updatedAt;
}
