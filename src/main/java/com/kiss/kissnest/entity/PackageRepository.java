package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PackageRepository {

    private Integer id;

    private Integer projectId;

    private Integer buildLogId;

    private String jarName;

    private String tarName;

    private String version;

    private String branch;

    private String tag;

    private Integer type;

    private Long buildAt;

    private Date createdAt;

    private Date updatedAt;
}
