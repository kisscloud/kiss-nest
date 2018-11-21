package com.kiss.kissnest.input;


import lombok.Data;

import java.util.List;

@Data
public class CreateMemberTeamInput {

    private Integer teamId;

    private List<MemberTeamInput> memberTeamInputs;
}
