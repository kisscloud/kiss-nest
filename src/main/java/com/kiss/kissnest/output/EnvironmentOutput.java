package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class EnvironmentOutput {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer type;

    private String typeText;

    private String path;

    private Integer serverCount;

    private String saltHost;

    private String saltVersion;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}
