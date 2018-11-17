package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectRepositoryOutput {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer projectId;

    private String projectName;

    private Integer repositoryId;

    private String sshUrl;

    private String httpUrl;

    private String lastCommit;

    private Integer commitCount;

    private Integer branchCount;

    private Integer memberCount;

    private Date createdAt;

    private Date updatedAt;
}
