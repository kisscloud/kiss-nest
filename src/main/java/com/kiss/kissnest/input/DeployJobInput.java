package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class DeployJobInput {

    private Integer projectId;

    private String branch;

    private String tag;

    private String remark;

    private Integer envId;
}
