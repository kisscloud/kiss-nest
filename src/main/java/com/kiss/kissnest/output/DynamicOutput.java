package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class DynamicOutput {

    private Integer id;

    private Integer teamId;

    private Integer groupId;

    private Integer projectId;

    private Integer targetType;

    private String log;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}
