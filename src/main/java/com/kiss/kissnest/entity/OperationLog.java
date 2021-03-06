package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OperationLog {

    private Integer id;

    private Integer teamId;

    private Integer type;

    private Integer targetId;

    private Integer targetType;

    private String beforeValue;

    private String afterValue;

    private Integer operatorId;

    private String operatorName;

    private Date recoveredAt;

    private Date createdAt;

    private Date updatedAt;
}
