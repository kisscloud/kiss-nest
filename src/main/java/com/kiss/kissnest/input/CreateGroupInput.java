package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateGroupInput {

    private String name;

    private Integer teamId;

    private Integer status;

    private String slug;
}
