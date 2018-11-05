package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateProjectInput {

    private String name;

    private Integer teamId;

    private Integer type;

    private String slug;
}
