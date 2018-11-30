package com.kiss.kissnest.output;

import lombok.Data;

import java.util.List;

@Data
public class GetDynamicOutput {

    private Integer count;

    private List<DynamicOutput> dynamicOutputs;
}
