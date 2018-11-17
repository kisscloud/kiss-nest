package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.mapper.ProjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProjectDaoImpl implements ProjectDao {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public Integer createProject(Project project) {

        return projectMapper.createProject(project);
    }

    @Override
    public Integer deleteProjectById(Integer id) {

        return projectMapper.deleteProjectById(id);
    }

    @Override
    public Integer updateProject(Project project) {

        return projectMapper.updateProject(project);
    }

    @Override
    public Integer addCount(Map map) {

        return projectMapper.addCount(map);
    }

    @Override
    public Project getProjectById(Integer id) {

        return projectMapper.getProjectById(id);
    }

    @Override
    public List<Project> getProjects(Integer teamId,Integer groupId) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("groupId",groupId);

        return projectMapper.getProjects(params);
    }

    @Override
    public Project getProjectByNameAndTeamId(String name,Integer teamId) {

        Map<String,Object> params = new HashMap<>();
        params.put("name",name);
        params.put("teamId",teamId);

        return projectMapper.getProjectByNameAndTeamId(params);
    }

    @Override
    public Project getProjectBySlugAndTeamId(String slug, Integer teamId) {

        Map<String,Object> params = new HashMap<>();
        params.put("slug",slug);
        params.put("teamId",teamId);

        return projectMapper.getProjectBySlugAndTeamId(params);
    }

    @Override
    public Integer addRepositoryIdById(Project project) {

        return projectMapper.addRepositoryIdById(project);
    }

    @Override
    public Project getProjectByRepositoryId(Integer repositoryId) {

        return projectMapper.getProjectByRepositoryId(repositoryId);
    }

    @Override
    public List<Project> getProjectsWithoutBuildJob(Integer teamId) {

        return projectMapper.getProjectsWithoutBuildJob(teamId);
    }

    @Override
    public List<Project> getProjectsWithBuildJob(Integer teamId) {

        return projectMapper.getProjectsWithBuildJob(teamId);
    }

    @Override
    public String getProjectOperatorAccessToken(Integer projectId) {

        return projectMapper.getProjectOperatorAccessToken(projectId);
    }

    @Override
    public String getProjectNameByServerId(String serverId) {

        return projectMapper.getProjectNameByServerId(serverId);
    }
}
