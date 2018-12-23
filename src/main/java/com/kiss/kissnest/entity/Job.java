package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Job {

    private Integer id;

    private Integer teamId;

    private Integer projectId;

    private String jobName;

    private String script;

    private Integer type;

    private Integer envId;

    private String serverIds;

    private Boolean userSupervisor;

    private String conf;

    private Integer status;

    private Integer number;

    private Date createdAt;

    private Date updatedAt;
}
