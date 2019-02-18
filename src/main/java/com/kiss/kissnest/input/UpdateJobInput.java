package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateJobInput {

    private Integer id;

    private Integer teamId;

    private Integer projectId;

    private String script;

    private Boolean useSupervisor;

    private String conf;

    private Integer type;

    private String workspace;
}
