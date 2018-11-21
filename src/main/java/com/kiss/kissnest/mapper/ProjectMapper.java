package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Project;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectMapper {

    Integer createProject(Project project);

    Integer deleteProjectById(Integer id);

    Integer updateProject(Project project);

    Integer addCount(Map map);

    Project getProjectById(Integer id);

    List<Project> getProjects(Map params);

    Project getProjectByNameAndTeamId(Map params);

    Project getProjectBySlugAndTeamId(Map params);

    Integer addRepositoryIdById(Project project);

    Project getProjectByRepositoryId(Integer repositoryId);

    List<Project> getProjectsWithoutBuildJob(Integer teamId);

    List<Project> getProjectsWithoutDeployJob(Integer teamId);

    List<Project> getProjectsWithBuildJob(Integer teamId);

    List<Project> getProjectsWithDeployJob(Integer teamId);

    String getProjectOperatorAccessToken(Integer projectId);

    String getProjectNameByServerId(String serverId);

    List<Project> getProjectsByGroupId(Integer groupId);
}
