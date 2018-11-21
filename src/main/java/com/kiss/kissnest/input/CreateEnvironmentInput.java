package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateEnvironmentInput {

    private Integer teamId;

    private String name;

    private String saltHost;

    private String saltToken;

    private Integer type;
}
