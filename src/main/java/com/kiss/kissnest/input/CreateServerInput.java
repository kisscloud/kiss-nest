package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateServerInput {

    private Integer teamId;

    private String name;

    private Integer envId;

    private String innerIp;

    private String outerIp;
}
