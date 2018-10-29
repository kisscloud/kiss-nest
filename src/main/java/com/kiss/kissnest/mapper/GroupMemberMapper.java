package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMemberMapper {

    Integer createGroupMember(GroupMember groupMember);

    Integer deleteGroupMemberById(Integer id);

    List<GroupMember> getGroupMembersByGroupId(Integer groupId);
}
