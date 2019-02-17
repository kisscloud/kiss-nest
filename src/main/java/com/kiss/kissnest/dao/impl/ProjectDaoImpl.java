package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.QueryProjectInput;
import com.kiss.kissnest.mapper.ProjectMapper;
import com.kiss.kissnest.output.ProjectOutput;
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
    public Integer addCount(Integer id, String type, Integer increments) {

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("type", type);
        params.put("increments", increments);

        return projectMapper.addCount(params);
    }

    @Override
    public Project getProjectById(Integer id) {

        return projectMapper.getProjectById(id);
    }

    @Override
    public List<Project> getProjects(Integer teamId, Integer groupId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("groupId", groupId);

        return projectMapper.getProjects(params);
    }

    @Override
    public Project getProjectByNameAndGroupIdAndTeamId(String name, Integer groupId, Integer teamId) {

        return projectMapper.getProjectByNameAndGroupIdAndTeamId(name, groupId, teamId);
    }

    @Override
    public Project getProjectBySlugAndGroupIdAndTeamId(String slug, Integer groupId,Integer teamId) {

        Map<String, Object> params = new HashMap<>();
        params.put("slug", slug);
        params.put("teamId", teamId);

        return projectMapper.getProjectBySlugAndGroupIdAndTeamId(slug,groupId,teamId);
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
    public List<Project> getProjectsWithBuildJobByTeamId(Integer teamId) {

        return projectMapper.getProjectsWithBuildJobByTeamId(teamId);
    }

    @Override
    public List<Project> getProjectsWithBuildJob(Integer teamId) {

        return projectMapper.getProjectsWithBuildJob(teamId);
    }

    @Override
    public List<Project> getProjectsWithDeployJob(Integer teamId) {

        return projectMapper.getProjectsWithDeployJob(teamId);
    }

    @Override
    public String getProjectOperatorAccessToken(Integer projectId) {

        return projectMapper.getProjectOperatorAccessToken(projectId);
    }

    @Override
    public String getProjectNameByServerId(String serverId) {

        return projectMapper.getProjectNameByServerId(serverId);
    }

    @Override
    public List<Project> getProjectsByGroupId(Integer groupId) {

        return projectMapper.getProjectsByGroupId(groupId);
    }

    @Override
    public List<ProjectOutput> getProjectOutputs(QueryProjectInput queryProjectInput) {

        return projectMapper.getProjectOutputs(queryProjectInput);
    }

    @Override
    public ProjectOutput getProjectOutputById(Integer id) {

        return projectMapper.getProjectOutputById(id);
    }

    @Override
    public Integer updateLastBuild(Integer id, String lastBuild) {

        return projectMapper.updateLastBuild(id, lastBuild);
    }

    @Override
    public Integer updateLastDeploy(Integer id, String lastDeploy) {

        return projectMapper.updateLastDeploy(id, lastDeploy);
    }

    @Override
    public Integer getProjectOutputsCount(QueryProjectInput queryProjectInput) {

        return projectMapper.getProjectOutputsCount(queryProjectInput);
    }
}
