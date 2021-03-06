package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Team;

import java.util.List;

public interface TeamDao {

    Integer createTeam(Team team);

    Integer deleteTeamById(Integer id);

    Integer updateTeam(Team team);

    Integer addCount(String type, Integer increments, Integer id);

    Team getTeamById(Integer id);

    Team getTeamByName(String name);

    Integer addRepositoryIdById(Team team);

    Integer getRepositoryIdByTeamId(Integer id);
}
