package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Member;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberMapper {

    Integer createMember(Member member);

    Integer deleteMemberById(Integer id);

    Integer updateMember(Member member);

    Integer addCount(Map map);

    Member getMemberById(Integer id);

    List<Member> getMembers();

    Member getMemberByAccountId(Integer accountId);

    Integer updateAccessTokenByAccountId(Map params);

    String getAccessTokenByAccountId(Integer accountId);

    Integer updateApiTokenByAccountId(Map params);
}
