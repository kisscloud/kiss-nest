package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class BuildLogOutput {

    private Integer id;

    private String jobName;

    private String branch;

    private Integer number;

    private String version;

    private Integer projectId;

    private String remark;

    private Integer status;

    private String output;

    private Integer operatorId;

    private String operatorName;

    private Long buildAt;
}
