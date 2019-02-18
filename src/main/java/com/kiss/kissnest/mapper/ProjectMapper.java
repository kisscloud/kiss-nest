package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.QueryProjectInput;
import com.kiss.kissnest.output.ProjectOutput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    Project getProjectByNameAndGroupIdAndTeamId(@Param("name") String name, @Param("groupId") Integer groupId, @Param("teamId") Integer teamId);

    Project getProjectBySlugAndGroupIdAndTeamId(@Param("slug") String slug, @Param("groupId") Integer groupId, @Param("teamId") Integer teamId);

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

    Integer updateLastBuild(@Param("id") Integer id, @Param("lastBuild") String lastBuild);

    Integer updateLastDeploy(@Param("id") Integer id, @Param("lastDeploy") String lastDeploy);

    Integer getProjectOutputsCount(QueryProjectInput queryProjectInput);
}
