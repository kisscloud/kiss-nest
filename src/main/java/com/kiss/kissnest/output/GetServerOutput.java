package com.kiss.kissnest.output;

import lombok.Data;

import java.util.List;

@Data
public class GetServerOutput {

    private Integer count;

    private List<ServerOutput> serverOutputs;
}
