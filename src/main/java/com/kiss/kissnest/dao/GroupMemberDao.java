package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.GroupMember;

import java.util.List;

public interface GroupMemberDao {

    Integer createGroupMember(GroupMember groupMember);

    Integer deleteGroupMemberById(Integer id);

    List<GroupMember> getGroupMembersByGroupId(Integer groupId);
}
