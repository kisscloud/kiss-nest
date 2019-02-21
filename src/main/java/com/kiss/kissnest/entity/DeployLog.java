package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DeployLog {

    private Integer id;

    private Integer teamId;

    private Integer jobId;

    private Integer envId;

    private String branch;

    private String tag;

    private String version;

    private Integer projectId;

    private String remark;

    private Integer successTasks;

    private Integer totalTasks;

    private Integer status;

    private String statusText;

    private String output;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
