package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.TeamGroup;

import java.util.List;

public interface TeamGroupDao {

    Integer createTeamGroup(TeamGroup teamGroup);

    Integer deleteTeamGroupById(Integer id);

    Integer updateTeamGroup(TeamGroup teamGroup);

    TeamGroup getTeamGroupById(Integer id);

    List<TeamGroup> getTeamGroups();

    List<TeamGroup> getTeamGroupsByTeamId(Integer teamId);
}
