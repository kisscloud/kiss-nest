package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.mapper.MemberMapper;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberDaoImpl implements MemberDao {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public Integer createMember(Member member) {

        return memberMapper.createMember(member);
    }

    @Override
    public Integer deleteMemberById(Integer id) {

        return memberMapper.deleteMemberById(id);
    }

    @Override
    public Integer updateMember(Member member) {

        return memberMapper.updateMember(member);
    }

    @Override
    public Integer addCount(Map map) {

        return memberMapper.addCount(map);
    }

    @Override
    public Member getMemberById(Integer id) {

        return memberMapper.getMemberById(id);
    }

    @Override
    public List<Member> getMembers() {

        return memberMapper.getMembers();
    }

    @Override
    public Member getMemberByAccountId(Integer accountId) {

        return memberMapper.getMemberByAccountId(accountId);
    }

    @Override
    public Integer updateAccessTokenByAccountId(Integer accountId, String accessToken) {

        Map<String,Object> params = new HashMap<>();
        params.put("accountId",accountId);
        params.put("accessToken",accessToken);

        Integer count = memberMapper.updateAccessTokenByAccountId(params);

        return count;
    }

    @Override
    public String getAccessTokenByAccountId(Integer accountId) {

        return memberMapper.getAccessTokenByAccountId(accountId);
    }

    @Override
    public Integer updateApiTokenByAccountId(Integer accountId, String apiToken) {

        Map<String,Object> params = new HashMap<>();
        params.put("accountId",accountId);
        params.put("apiToken",apiToken);

        Integer count = memberMapper.updateApiTokenByAccountId(params);

        return count;
    }

    @Override
    public Team getMemberDefaultTeamId(Integer accountId) {

        return memberMapper.getMemberDefaultTeamId(accountId);
    }
}
