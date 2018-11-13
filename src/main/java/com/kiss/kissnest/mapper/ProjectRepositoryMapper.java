package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.ProjectRepository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectRepositoryMapper {

    Integer createProjectRepository(ProjectRepository projectRepository);

    ProjectRepository getProjectRepositoryByProjectId(Integer projectId);

    List<ProjectRepository> getProjectRepositoryByTeamId(Integer teamId);
}
