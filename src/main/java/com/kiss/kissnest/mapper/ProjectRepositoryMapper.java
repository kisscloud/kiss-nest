package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.ProjectRepository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProjectRepositoryMapper {

    Integer createProjectRepository(ProjectRepository projectRepository);

    ProjectRepository getProjectRepositoryByProjectId(Integer projectId);

    List<ProjectRepository> getProjectRepositoriesByTeamId(Integer teamId);

    Integer addCount(Map params);

    Integer updateProjectRepositoryBranch(Map map);

    Integer deleteProjectRepositoryById(Integer id);
}
