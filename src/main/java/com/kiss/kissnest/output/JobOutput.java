package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class JobOutput {

    private Integer id;

    private Integer teamId;

    private Integer projectId;

    private String jobName;

    private String script;

    private Integer type;

    private Integer envId;

    private String serverIds;

    private Long createdAt;

    private Long updatedAt;
}
