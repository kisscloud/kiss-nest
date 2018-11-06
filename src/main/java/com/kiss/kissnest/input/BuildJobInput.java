package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class BuildJobInput {

    private Integer projectId;

    private String branch;
}
