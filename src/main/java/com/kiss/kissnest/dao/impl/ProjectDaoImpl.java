package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public List<Project> getProjects() {

        return projectMapper.getProjects();
    }

    @Override
    public List<Project> getProjectsByName(String name) {

        return projectMapper.getProjectsByName(name);
    }
}
