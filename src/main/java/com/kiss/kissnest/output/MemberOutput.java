package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class MemberOutput {

    private Integer id;

    private Integer accountId;

    private Integer groupsCount;

    private Integer projectsCount;

    private Integer needsCount;

    private Integer issuesCount;

    private Integer operatorId;

    private String operatorName;

    private Long createdAt;

    private Long updatedAt;
}
