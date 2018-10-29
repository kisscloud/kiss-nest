package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.GroupMemberDao;
import com.kiss.kissnest.entity.GroupMember;
import com.kiss.kissnest.mapper.GroupMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GroupMemberDaoImpl implements GroupMemberDao {

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    public Integer createGroupMember(GroupMember groupMember) {

        return groupMemberMapper.createGroupMember(groupMember);
    }

    @Override
    public Integer deleteGroupMemberById(Integer id) {

        return groupMemberMapper.deleteGroupMemberById(id);
    }

    @Override
    public List<GroupMember> getGroupMembersByGroupId(Integer groupId) {

        return groupMemberMapper.getGroupMembersByGroupId(groupId);
    }
}
