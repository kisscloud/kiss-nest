package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.MemberTeam;
import com.kiss.kissnest.entity.Team;

import java.util.List;


public interface MemberTeamDao {

    Integer createMemberTeam(MemberTeam memberTeam);

    List<Team> getMemberTeams(Integer accountId);
}
