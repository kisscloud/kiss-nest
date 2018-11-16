package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.MemberGroup;

import java.util.List;
import java.util.Map;

public interface MemberGroupDao {

    Integer createMemberGroup(MemberGroup memberGroup);

    Integer createMemberGroups(List<MemberGroup> memberGroups);

    List<MemberGroup> getMemberGroups(Integer teamId, Integer groupId);

    MemberGroup getMemberGroup(Integer teamId, Integer groupId, Integer memberId);

}
