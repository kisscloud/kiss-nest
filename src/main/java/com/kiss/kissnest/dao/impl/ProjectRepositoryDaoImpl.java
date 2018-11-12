package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.mapper.ProjectRepositoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectRepositoryDaoImpl implements ProjectRepositoryDao {

    @Autowired
    private ProjectRepositoryMapper projectRepositoryMapper;

    @Override
    public Integer createProjectRepository(ProjectRepository projectRepository) {

        return projectRepositoryMapper.createProjectRepository(projectRepository);
    }

    @Override
    public ProjectRepository getProjectRepositoryByProjectId(Integer projectId) {

        return projectRepositoryMapper.getProjectRepositoryByProjectId(projectId);
    }

    @Override
    public List<ProjectRepository> getProjectRepositoryByTeamId(Integer teamId) {

        return projectRepositoryMapper.getProjectRepositoryByTeamId(teamId);
    }
}
