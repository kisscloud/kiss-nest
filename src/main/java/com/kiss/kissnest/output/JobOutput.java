package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class JobOutput {

    private Integer id;

    private Integer teamId;

    private Integer projectId;

    private String projectName;

    private String groupName;

    private Integer groupId;

    private String jobName;

    private String script;

    private Integer type;

    private Integer envId;

    private String envName;

    private String serverIds;

    private Boolean useSupervisor;

    private String conf;

    private Long createdAt;

    private Long updatedAt;
}
