package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.GroupProject;

import java.util.List;

public interface GroupProjectDao {

    Integer createGroupProject(GroupProject groupProject);

    Integer deleteGroupProjectById(Integer id);

    Integer updateGroupProjectById(GroupProject groupProject);

    GroupProject getGroupProjectById(Integer id);

    List<GroupProject> getGroupsProjects();

    List<GroupProject> getGroupProjectsByGroupId(Integer groupId);
}
