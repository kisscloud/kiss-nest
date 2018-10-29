package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.GroupProject;

import java.util.List;

public interface GroupProjectDao {

    Integer createGroupProject(GroupProject groupProject);

    Integer createGroupProjects(List<GroupProject> groupProjects);

    Integer deleteGroupProjectById(Integer id);

    Integer deleteGroupProjectByGroupId(Integer groupId);

    Integer updateGroupProjectById(GroupProject groupProject);

    GroupProject getGroupProjectById(Integer id);

    List<GroupProject> getGroupsProjects();

    List<GroupProject> getGroupProjectsByGroupId(Integer groupId);
}
