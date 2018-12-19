package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GroupDaoImpl implements GroupDao {

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public Integer createGroup(Group group) {

        return groupMapper.createGroup(group);
    }

    @Override
    public Integer deleteGroupById(Integer id) {

        return groupMapper.deleteGroupById(id);
    }

    @Override
    public Integer updateGroupById(Group group) {

        return groupMapper.updateGroupById(group);
    }

    @Override
    public Integer addCount(Integer id,String type,Integer increment) {

        Map<String,Object> params = new HashMap<>();
        params.put("id",id);
        params.put("type",type);
        params.put("increment",increment);

        return groupMapper.addCount(params);
    }

    @Override
    public Group getGroupById(Integer id) {

        return groupMapper.getGroupById(id);
    }

    @Override
    public List<Group> getGroups(Integer teamId) {

        return groupMapper.getGroups(teamId);
    }

    @Override
    public Group getGroupByNameAndTeamId(String name,Integer teamId) {

        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("teamId",teamId);

        return groupMapper.getGroupByNameAndTeamId(params);
    }

    @Override
    public Group getGroupBySlugAndTeamId(String slug, Integer teamId) {

        Map<String,Object> params = new HashMap<>();
        params.put("slug",slug);
        params.put("teamId",teamId);

        return groupMapper.getGroupBySlugAndTeamId(params);
    }

    @Override
    public Integer addRepositoryIdById(Group group) {

        return groupMapper.addRepositoryIdById(group);
    }

    @Override
    public Group getGroupByProjectId(Integer projectId) {

        return groupMapper.getGroupByProjectId(projectId);
    }
}
