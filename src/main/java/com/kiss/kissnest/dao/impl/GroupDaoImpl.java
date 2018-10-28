package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Integer addCount(Map params) {

        return groupMapper.addCount(params);
    }

    @Override
    public Group getGroupById(Integer id) {

        return groupMapper.getGroupById(id);
    }

    @Override
    public List<Group> getGroups() {

        return groupMapper.getGroups();
    }

    @Override
    public List<Group> getGroupsByName(String name) {

        return groupMapper.getGroupsByName(name);
    }
}
