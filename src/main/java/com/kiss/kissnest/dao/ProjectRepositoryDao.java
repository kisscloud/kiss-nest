package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.ProjectRepository;

import java.util.List;

public interface ProjectRepositoryDao {

    Integer createProjectRepository(ProjectRepository projectRepository);

    ProjectRepository getProjectRepositoryByProjectId(Integer projectId);

    List<ProjectRepository> getProjectRepositoryByTeamId(Integer teamId);
}
