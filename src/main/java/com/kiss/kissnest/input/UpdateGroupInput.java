package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class UpdateGroupInput {

    private Integer id;

    private String name;

    private Integer teamId;

    private Integer status;
}
