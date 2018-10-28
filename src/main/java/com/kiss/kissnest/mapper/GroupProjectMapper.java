package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.GroupProject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupProjectMapper {

    Integer createGroupProject(GroupProject groupProject);

    Integer deleteGroupProjectById(Integer id);

    Integer updateGroupProjectById(GroupProject groupProject);

    GroupProject getGroupProjectById(Integer id);

    List<GroupProject> getGroupsProjects();

    List<GroupProject> getGroupProjectsByGroupId(Integer groupId);
}
