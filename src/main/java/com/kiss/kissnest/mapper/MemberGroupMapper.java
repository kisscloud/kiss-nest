package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.MemberGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberGroupMapper {

    Integer createMemberGroup(MemberGroup memberGroup);

    Integer createMemberGroups(List<MemberGroup> memberGroups);

    List<MemberGroup> getMemberGroups(Map params);

    MemberGroup getMemberGroup(Map params);

}
