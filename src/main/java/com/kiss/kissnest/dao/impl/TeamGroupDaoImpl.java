package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.TeamGroupDao;
import com.kiss.kissnest.entity.TeamGroup;
import com.kiss.kissnest.mapper.TeamGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamGroupDaoImpl implements TeamGroupDao {

    @Autowired
    private TeamGroupMapper teamGroupMapper;

    @Override
    public Integer createTeamGroup(TeamGroup teamGroup) {

        return teamGroupMapper.createTeamGroup(teamGroup);
    }

    @Override
    public Integer deleteTeamGroupById(Integer id) {

        return teamGroupMapper.deleteTeamGroupById(id);
    }

    @Override
    public Integer updateTeamGroup(TeamGroup teamGroup) {

        return teamGroupMapper.updateTeamGroup(teamGroup);
    }

    @Override
    public TeamGroup getTeamGroupById(Integer id) {

        return teamGroupMapper.getTeamGroupById(id);
    }

    @Override
    public List<TeamGroup> getTeamGroups() {

        return teamGroupMapper.getTeamGroups();
    }

    @Override
    public List<TeamGroup> getTeamGroupsByTeamId(Integer teamId) {

        return teamGroupMapper.getTeamGroupsByTeamId(teamId);
    }
}
