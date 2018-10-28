package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BuildLog {

    private Integer id;

    private String branch;

    private String version;

    private Integer projectId;

    private String remark;

    private Integer status;

    private String output;

    private Integer operationId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
