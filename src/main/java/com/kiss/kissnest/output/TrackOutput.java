package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class TrackOutput {

    private Integer id;

    private Integer teamId;

    private String authorName;

    private String authorEmail;

    private Integer type;

    private String hash;

    private String ref;

    private Integer projectId;

    private String projectName;

    private String message;

    private String modified;

    private Date operatorAt;

    private Date createdAt;

    private Date updatedAt;
}
