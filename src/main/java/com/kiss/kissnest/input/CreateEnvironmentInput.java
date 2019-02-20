package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateEnvironmentInput {

    private Integer teamId;

    private String name;

    private String path;

    private String saltHost;

    private String saltVersion;

    private String saltUser;

    private String saltPassword;

    private Integer type;
}
