package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateProjectInput {

    private Integer id;

    private String name;

    private Integer teamId;

    private Integer groupId;
}
