package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateEnvironmentInput {

    private Integer teamId;

    private String name;

    private Integer type;
}
