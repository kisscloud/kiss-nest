package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.QueryProjectInput;
import com.kiss.kissnest.output.ProjectOutput;

import java.util.List;

public interface ProjectDao {

    Integer createProject(Project project);

    Integer deleteProjectById(Integer id);

    Integer updateProject(Project project);

    Integer addCount(Integer id, String type, Integer increments);

    Project getProjectById(Integer id);

    List<Project> getProjects(Integer teamId, Integer groupId);

    Project getProjectByNameAndGroupIdAndTeamId(String name, Integer groupId, Integer teamId);

    Project getProjectBySlugAndGroupIdAndTeamId(String slug, Integer groupId, Integer teamId);

    Integer addRepositoryIdById(Project project);

    Project getProjectByRepositoryId(Integer repositoryId);

    List<Project> getProjectsWithoutBuildJob(Integer teamId);

    List<Project> getProjectsWithBuildJobByTeamId(Integer teamId);

    List<Project> getProjectsWithBuildJob(Integer teamId);

    List<Project> getProjectsWithDeployJob(Integer teamId);

    String getProjectOperatorAccessToken(Integer projectId);

    String getProjectNameByServerId(String serverId);

    List<Project> getProjectsByGroupId(Integer groupId);

    List<ProjectOutput> getProjectOutputs(QueryProjectInput queryProjectInput);

    ProjectOutput getProjectOutputById(Integer id);

    Integer updateLastBuild(Integer id, String lastBuild);

    Integer updateLastDeploy(Integer id, String lastDeploy);

    Integer getProjectOutputsCount(QueryProjectInput queryProjectInput);
}
