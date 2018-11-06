package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Group;

import java.util.List;
import java.util.Map;

public interface GroupDao {

    Integer createGroup(Group group);

    Integer deleteGroupById(Integer id);

    Integer updateGroupById(Group group);

    Integer addCount(Map params);

    Group getGroupById(Integer id);

    List<Group> getGroups(Integer teamId);

    Group getGroupByNameAndTeamId(String name,Integer teamId);

    Group getGroupBySlugAndTeamId(String slug,Integer teamId);

    Integer addRepositoryIdById(Group group);
}
