package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamMapper {

    Integer createTeam(Team team);

    Integer deleteTeamById(Integer id);

    Integer updateTeam(Team team);

    Integer addCount(@Param("type") String type, @Param("increments") Integer increments, @Param("id") Integer id);

    Team getTeamById(Integer id);

    Team getTeamByName(String name);

    Integer addRepositoryIdById(Team team);

    Integer getRepositoryIdByTeamId(Integer id);
}
