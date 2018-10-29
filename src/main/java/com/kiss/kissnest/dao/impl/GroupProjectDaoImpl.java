package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.GroupProjectDao;
import com.kiss.kissnest.entity.GroupProject;
import com.kiss.kissnest.mapper.GroupProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupProjectDaoImpl implements GroupProjectDao {

    @Autowired
    private GroupProjectMapper groupProjectMapper;

    @Override
    public Integer createGroupProject(GroupProject groupProject) {

        return groupProjectMapper.createGroupProject(groupProject);
    }

    @Override
    public Integer createGroupProjects(List<GroupProject> groupProjects) {

        return groupProjectMapper.createGroupProjects(groupProjects);
    }

    @Override
    public Integer deleteGroupProjectById(Integer id) {

        return groupProjectMapper.deleteGroupProjectById(id);
    }

    @Override
    public Integer deleteGroupProjectByGroupId(Integer groupId) {

        return groupProjectMapper.deleteGroupProjectByGroupId(groupId);
    }

    @Override
    public Integer updateGroupProjectById(GroupProject groupProject) {

        return groupProjectMapper.updateGroupProjectById(groupProject);
    }

    @Override
    public GroupProject getGroupProjectById(Integer id) {

        return groupProjectMapper.getGroupProjectById(id);
    }

    @Override
    public List<GroupProject> getGroupsProjects() {

        return groupProjectMapper.getGroupsProjects();
    }

    @Override
    public List<GroupProject> getGroupProjectsByGroupId(Integer groupId) {

        return groupProjectMapper.getGroupProjectsByGroupId(groupId);
    }
}
