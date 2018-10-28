package com.kiss.kissnest.output;

import lombok.Data;

import java.util.Date;

@Data
public class TeamOutput {

    private Integer id;

    private String name;

    private Integer groupsCount;

    private Integer operatorId;

    private String operatorName;

    private Date createdAt;

    private Date updatedAt;
}
