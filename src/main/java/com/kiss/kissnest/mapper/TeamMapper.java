package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Team;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeamMapper {

    Integer createTeam(Team team);

    Integer deleteTeamById(Integer id);

    Integer updateTeam(Team team);

    Integer addGroupsCount(Integer id);

    Team getTeamById(Integer id);

    List<Team> getTeams();
}
