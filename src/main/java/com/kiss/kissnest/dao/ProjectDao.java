package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Project;

import java.util.List;
import java.util.Map;

public interface ProjectDao {

    Integer createProject(Project project);

    Integer deleteProjectById(Integer id);

    Integer updateProject(Project project);

    Integer addCount(Map map);

    Project getProjectById(Integer id);

    List<Project> getProjects(Integer teamId);

    Project getProjectByNameAndTeamId(String name,Integer teamId);

    Project getProjectBySlugAndTeamId(String slug,Integer teamId);
}
