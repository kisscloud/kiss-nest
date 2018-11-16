package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Team;

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

    Integer updateAccessTokenByAccountId(Integer accountId,String accessToken);

    String getAccessTokenByAccountId(Integer accountId);

    Integer updateApiTokenByAccountId(Integer accountId,String apiToken);

    Team getMemberDefaultTeamId(Integer accountId);

    Integer createMembers(List<Member> members);
}
