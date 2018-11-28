package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class DeployLogOutput {

    private Integer id;

    private Integer teamId;

    private String jobId;

    private String envId;

    private String serverIds;

    private String branch;

    private String tag;

    private String version;

    private Integer projectId;

    private String projectName;

    private String remark;

    private Integer status;

    private String output;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}
