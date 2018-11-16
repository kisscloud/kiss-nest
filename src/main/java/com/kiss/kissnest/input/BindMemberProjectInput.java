package com.kiss.kissnest.input;

import lombok.Data;

import java.util.List;

@Data
public class BindMemberProjectInput {

    private Integer teamId;

    private Integer projectId;

    private List<MemberProjectInput> memberProjectInputs;
}
