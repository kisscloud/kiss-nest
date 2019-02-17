package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateJobInput {

    private Integer projectId;

    private String script;

    private Integer type;

    private String workspace;
}
