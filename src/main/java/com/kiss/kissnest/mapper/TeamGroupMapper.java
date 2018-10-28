package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.TeamGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeamGroupMapper {

    Integer createTeamGroup(TeamGroup teamGroup);

    Integer deleteTeamGroupById(Integer id);

    Integer updateTeamGroup(TeamGroup teamGroup);

    TeamGroup getTeamGroupById(Integer id);

    List<TeamGroup> getTeamGroups();

    List<TeamGroup> getTeamGroupsByTeamId(Integer teamId);
}
