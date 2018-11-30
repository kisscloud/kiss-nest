package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class DeployLogOutput {

    private Integer id;

    private Integer teamId;

    private String jobId;

    private String envId;

    private String envName;

    private String serverIds;

    private String branch;

    private String tag;

    private String version;

    private Integer projectId;

    private String projectName;

    private String remark;

    private Integer status;

    private String statusText;

    private Integer operatorId;

    private String operatorName;

    private Integer groupId;

    private String groupName;

    private String branchPath;

    private Long createdAt;

    private Long updatedAt;
}
