package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectRepository {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer projectId;

    private Integer repositoryId;

    private String sshUrl;

    private String httpUrl;

    private String pathWithNamespace;

    private String lastCommit;

    private Integer commitCount;

    private Integer branchCount;

    private Integer memberCount;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
