package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
