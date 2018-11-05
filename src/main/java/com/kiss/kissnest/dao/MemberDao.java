package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Member;

import java.util.List;
import java.util.Map;

public interface MemberDao {

    Integer createMember(Member member);

    Integer deleteMemberById(Integer id);

    Integer updateMember(Member member);

    Integer addCount(Map map);

    Member getMemberById(Integer id);

    List<Member> getMembers();

    Member getMemberByAccountId(Integer accountId);
}
