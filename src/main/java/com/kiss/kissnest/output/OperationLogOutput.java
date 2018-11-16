package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class OperationLogOutput {

    private Integer id;

    private Integer teamId;

    private Integer type;

    private Integer targetType;

    private String targetTypeText;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}
