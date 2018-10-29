package com.kiss.kissnest.input;

import lombok.Data;

import java.util.List;

@Data
public class BindGroupProjectsInput {

    private Integer groupId;

    private List<Integer> projectIds;
}
