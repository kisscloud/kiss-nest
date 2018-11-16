package com.kiss.kissnest.input;

import lombok.Data;

import java.util.List;

@Data
public class BindMemberGroupInput {

    private Integer teamId;

    private Integer groupId;

    private List<MemberGroupInput> memberGroupInputs;
}
