package com.kiss.kissnest.output;

import lombok.Data;

import java.util.List;

@Data
public class GetDeployLogOutput {

    private Integer count;

    private List<DeployLogOutput> deployLogOutputs;
}
