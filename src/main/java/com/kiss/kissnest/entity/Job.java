package com.kiss.kissnest.entity;

import lombok.Data;

@Data
public class Job {

    private Integer id;

    private Integer projectId;

    private String jobName;

    private String shell;

    private Integer type;
}
