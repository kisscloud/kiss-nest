package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class BuildLogOutput {

    private Integer id;

    private Integer teamId;

    private String jobName;

    private String branch;

    private String branchPath;

    private Integer number;

    private String version;

    private Integer projectId;

    private String projectName;

    private Integer groupId;

    private String groupName;

    private String remark;

    private Integer duration;

    private String logUrl;

    private Integer status;

    private String statusText;

    private Long queueId;

    private String commitPath;

    private Integer operatorId;

    private String operatorName;

    private Long buildAt;

    private Long createdAt;

    private Long updatedAt;
}
