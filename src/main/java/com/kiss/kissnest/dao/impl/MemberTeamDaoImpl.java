package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberTeamDao;
import com.kiss.kissnest.entity.MemberTeam;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.mapper.MemberTeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberTeamDaoImpl implements MemberTeamDao {

    @Autowired
    private MemberTeamMapper memberTeamMapper;

    @Override
    public Integer createMemberTeam(MemberTeam memberTeam) {

        return memberTeamMapper.createMemberTeam(memberTeam);
    }

    @Override
    public List<Team> getMemberTeams(Integer accountId) {

        return memberTeamMapper.getMemberTeams(accountId);
    }
}
