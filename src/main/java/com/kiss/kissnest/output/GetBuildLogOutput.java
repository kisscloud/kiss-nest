package com.kiss.kissnest.output;

import lombok.Data;

import java.util.List;

@Data
public class GetBuildLogOutput {

    private Integer count;

    private List<BuildLogOutput> buildLogOutputs;
}
