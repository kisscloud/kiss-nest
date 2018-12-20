package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.MemberTeam;
import com.kiss.kissnest.entity.Team;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberTeamMapper {

    Integer createMemberTeam(MemberTeam memberTeam);

    Integer createMemberTeams(List<MemberTeam> memberTeams);

    List<Team> getMemberTeams(Integer accountId);

    MemberTeam getMemberTeam(Map params);

    List<Member> getMemberTeamsByTeamId(Integer teamId);
}
