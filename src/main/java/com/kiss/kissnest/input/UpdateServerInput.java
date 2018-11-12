package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateServerInput {

    private Integer id;

    private Integer teamId;

    private String name;

    private Integer envId;

    private String innerIp;

    private String outerIp;
}
