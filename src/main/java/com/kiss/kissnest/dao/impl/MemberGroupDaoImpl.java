package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberGroupDao;
import com.kiss.kissnest.entity.MemberGroup;
import com.kiss.kissnest.mapper.MemberGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberGroupDaoImpl implements MemberGroupDao {

    @Autowired
    private MemberGroupMapper memberGroupMapper;

    @Override
    public Integer createMemberGroup(MemberGroup memberGroup) {

        return memberGroupMapper.createMemberGroup(memberGroup);
    }

    @Override
    public Integer createMemberGroups(List<MemberGroup> memberGroups) {

        return memberGroupMapper.createMemberGroups(memberGroups);
    }

    @Override
    public List<MemberGroup> getMemberGroups(Integer teamId, Integer groupId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("groupId", groupId);

        return memberGroupMapper.getMemberGroups(params);
    }

    @Override
    public MemberGroup getMemberGroup(Integer teamId, Integer groupId, Integer memberId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("groupId", groupId);
        params.put("memberId", memberId);

        return memberGroupMapper.getMemberGroup(params);
    }
}
