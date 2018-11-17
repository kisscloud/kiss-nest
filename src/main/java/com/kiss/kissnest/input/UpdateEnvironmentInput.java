package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateEnvironmentInput {

    private Integer teamId;

    private Integer envId;

    private String name;
}
