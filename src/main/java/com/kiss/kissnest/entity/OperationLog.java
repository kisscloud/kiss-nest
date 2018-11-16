package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OperateLog {

    private Integer id;

    private Integer teamId;

    private Integer type;

    private Integer targetId;

    private String oldValue;

    private String newValue;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
