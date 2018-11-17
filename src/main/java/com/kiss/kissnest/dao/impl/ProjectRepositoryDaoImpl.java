package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.mapper.ProjectRepositoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Integer addCount(String type, Integer increment) {

        Map<String,Object> params = new HashMap<>();
        params.put("type",type);
        params.put("increment",increment);

        return projectRepositoryMapper.addCount(params);
    }

    @Override
    public Integer updateProjectRepositoryBranch(Integer teamId, Integer projectId, Integer branchCount) {

        Map<String,Object> params = new HashMap<>();
        params.put("teamId",teamId);
        params.put("projectId",projectId);
        params.put("branchCount",branchCount);

        return projectRepositoryMapper.updateProjectRepositoryBranch(params);
    }
}
