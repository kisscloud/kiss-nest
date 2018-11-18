package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateEnvironmentInput {

    private Integer id;

    private Integer envId;

    private String name;
}
