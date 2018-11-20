package com.kiss.kissnest.input;

import lombok.Data;

import java.util.List;

@Data
public class UpdateDeployInput {

    private Integer id;

    private Integer envId;

    private List<Integer> serverIds;

    private String conf;

    private Integer type;
}
