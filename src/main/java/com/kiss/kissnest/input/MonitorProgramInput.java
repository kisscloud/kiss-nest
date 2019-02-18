package com.kiss.kissnest.input;

import lombok.Data;

import java.util.List;

@Data
public class MonitorProgramInput {
    private Boolean ping;
    private List<Object> components;
}
