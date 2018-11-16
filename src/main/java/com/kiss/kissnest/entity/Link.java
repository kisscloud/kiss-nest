package com.kiss.kissnest.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Link {
    private Integer id;

    private Integer teamId;

    private String title;

    private String url;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
