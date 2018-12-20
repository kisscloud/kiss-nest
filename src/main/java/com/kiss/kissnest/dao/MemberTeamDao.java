package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.MemberTeam;
import com.kiss.kissnest.entity.Team;

import java.util.List;


public interface MemberTeamDao {

    Integer createMemberTeam(MemberTeam memberTeam);

    Integer createMemberTeams(List<MemberTeam> memberTeams);

    List<Team> getMemberTeams(Integer accountId);

    MemberTeam getMemberTeam(Integer teamId,Integer memberId);

    List<Member> getMemberTeamsByTeamId(Integer teamId);
}
