package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class QueryProjectInput {

    private Integer teamId;

    private Integer groupId;

    private Integer type;

    private String orderFiledName;

    private Integer orderType;

    private Integer page;

    private Integer size;
}
