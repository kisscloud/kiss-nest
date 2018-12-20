package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberTeamDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.MemberTeam;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.mapper.MemberTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberTeamDaoImpl implements MemberTeamDao {

    @Autowired
    private MemberTeamMapper memberTeamMapper;

    @Override
    public Integer createMemberTeam(MemberTeam memberTeam) {

        return memberTeamMapper.createMemberTeam(memberTeam);
    }

    @Override
    public Integer createMemberTeams(List<MemberTeam> memberTeams) {

        return memberTeamMapper.createMemberTeams(memberTeams);
    }

    @Override
    public List<Team> getMemberTeams(Integer accountId) {

        return memberTeamMapper.getMemberTeams(accountId);
    }

    @Override
    public MemberTeam getMemberTeam(Integer teamId, Integer memberId) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("memberId",memberId);

        return memberTeamMapper.getMemberTeam(params);
    }

    @Override
    public List<Member> getMemberTeamsByTeamId(Integer teamId) {
        return memberTeamMapper.getMemberTeamsByTeamId(teamId);
    }
}
