package com.kiss.kissnest.output;

import lombok.Data;

import java.util.List;

@Data
public class BindGroupProjectsOutput {

    private Integer groupId;

    private List<Integer> projectIds;
}
