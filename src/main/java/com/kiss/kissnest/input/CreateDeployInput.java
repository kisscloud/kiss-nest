package com.kiss.kissnest.input;

import lombok.Data;

import java.util.List;

@Data
public class CreateDeployInput {

    private Integer teamId;

    private Integer projectId;

    private Integer envId;

    private List<String> serverIds;

    private String script;

    private Integer type;
}
