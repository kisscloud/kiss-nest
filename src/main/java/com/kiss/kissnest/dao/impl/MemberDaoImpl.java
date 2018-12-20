package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.mapper.MemberMapper;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.apache.ibatis.annotations.Mapper;
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
    public Integer addCount(Integer id, Integer increment, String type) {

        Map<String, Object> map = new HashMap();
        map.put("id", id);
        map.put("increment", increment);
        map.put("type", type);

        return memberMapper.addCount(map);
    }

    @Override
    public Member getMemberById(Integer id) {

        return memberMapper.getMemberById(id);
    }

    @Override
    public List<Member> getMembers(Integer teamId, Integer groupId, Integer projectId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("groupId", groupId);
        params.put("projectId", projectId);

        return memberMapper.getMembers(params);
    }

    @Override
    public Member getMemberByAccountId(Integer accountId) {

        return memberMapper.getMemberByAccountId(accountId);
    }

    @Override
    public Integer updateAccessTokenByAccountId(Integer accountId, String accessToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("accessToken", accessToken);

        Integer count = memberMapper.updateAccessTokenByAccountId(params);

        return count;
    }

    @Override
    public String getAccessTokenByAccountId(Integer accountId) {

        return memberMapper.getAccessTokenByAccountId(accountId);
    }

    @Override
    public Integer updateApiTokenByAccountId(Integer accountId, String apiToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("apiToken", apiToken);

        Integer count = memberMapper.updateApiTokenByAccountId(params);

        return count;
    }

    @Override
    public Team getMemberDefaultTeamId(Integer accountId) {

        return memberMapper.getMemberDefaultTeamId(accountId);
    }

    @Override
    public Integer createMembers(List<Member> members) {

        return memberMapper.createMembers(members);
    }

    @Override
    public List<Member> getGroupValidMembers(Integer groupId, String name) {

        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("name", name + "%");

        return memberMapper.getGroupValidMembers(params);
    }

    @Override
    public List<Member> getProjectValidMembers(Integer projectId, String name) {

        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("name", name + "%");

        return memberMapper.getProjectValidMembers(params);
    }

    @Override
    public Integer deleteCount(Integer id, Integer incrememt, String type) {

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("increment", incrememt);
        params.put("type", type);

        return memberMapper.deleteCount(params);
    }
}
