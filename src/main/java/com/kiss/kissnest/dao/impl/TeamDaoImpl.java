package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.TeamDao;
import com.kiss.kissnest.entity.Team;
import com.kiss.kissnest.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamDaoImpl implements TeamDao {

    @Autowired
    private TeamMapper teamMapper;

    @Override
    public Integer createTeam(Team team) {

        return teamMapper.createTeam(team);
    }

    @Override
    public Integer deleteTeamById(Integer id) {

        return teamMapper.deleteTeamById(id);
    }

    @Override
    public Integer updateTeam(Team team) {

        return teamMapper.updateTeam(team);
    }

    @Override
    public Integer addGroupsCount(Integer id) {

        return teamMapper.addGroupsCount(id);
    }

    @Override
    public Team getTeamById(Integer id) {

        return teamMapper.getTeamById(id);
    }

    @Override
    public List<Team> getTeams(Integer accountId) {

        return teamMapper.getTeams(accountId);
    }

    @Override
    public Team getTeamByName(String name) {

        return teamMapper.getTeamByName(name);
    }

    @Override
    public Integer addRepositoryIdById(Team team) {

        return teamMapper.addRepositoryIdById(team);
    }

    @Override
    public Integer getRepositoryIdByTeamId(Integer id) {

        return teamMapper.getRepositoryIdByTeamId(id);
    }
}
