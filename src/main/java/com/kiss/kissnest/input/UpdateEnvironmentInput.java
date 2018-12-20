package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateEnvironmentInput {

    private Integer teamId;

    private Integer id;

    private String name;

    private String saltHost;

    private String saltVersion;

    private String saltUser;

    private Integer type;

    private String saltPassword;
}
