package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DeployLog {

    private Integer id;

    private String serverIds;

    private String branch;

    private String version;

    private Integer projectId;

    private String remark;

    private Integer status;

    private Integer operationId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
