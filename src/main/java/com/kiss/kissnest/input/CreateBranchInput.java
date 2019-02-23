package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateBranchInput {

    private Integer projectId;

    private String branchName;

    private String ref;
}
