package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectOutput {

    private Integer id;

    private String name;

    private Integer teamId;

    private Integer groupId;

    private String groupName;

    private Integer membersCount;

    private Integer needsCount;

    private Integer issuesCount;

    private Integer operatorId;

    private String operatorName;

    private Integer type;

    private String typeText;

    private String slug;

    private String lastBuild;

    private String lastBuildUrl;

    private String lastDeploy;

    private String lastDeployUrl;

    private Long createdAt;

    private Long updatedAt;

    public String getLastBuild() {
        return lastBuild == null ? null : lastBuild.substring(0, 8);
    }
}
