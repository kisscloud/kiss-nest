package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class GroupProjectOutput {

    private Integer id;

    private Integer groupId;

    private Integer projectId;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}
