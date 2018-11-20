package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DeployVersion {

    private Integer id;

    private Integer projectId;

    private String serverId;

    private String branch;

    private String tag;

    private Integer type;

    private String version;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
