package com.kiss.kissnest.output;

import lombok.Data;

import java.util.List;

@Data
public class ProjectOutputs {

    List<ProjectOutput> projectOutputs;
    Integer count;

    public ProjectOutputs(List<ProjectOutput> projectOutputs, Integer count) {
        this.projectOutputs = projectOutputs;
        this.count = count;
    }
}
