package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.MemberTeam;
import com.kiss.kissnest.entity.Team;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberTeamMapper {

    Integer createMemberTeam(MemberTeam memberTeam);

    List<Team> getMemberTeams(Integer accountId);
}
