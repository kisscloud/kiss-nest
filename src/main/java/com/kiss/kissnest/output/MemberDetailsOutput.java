package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class MemberDetailsOutput {

    private Integer id;

    private String name;

    private Integer groupId;

    private String groupName;

    private Integer projectId;

    private String projectName;

    private Integer needsCount;

    private Integer issuesCount;
}
