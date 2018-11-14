package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class EnvironmentOutput {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}