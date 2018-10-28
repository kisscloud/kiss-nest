package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProjectMemberDao;
import com.kiss.kissnest.entity.ProjectMember;
import com.kiss.kissnest.mapper.ProjectMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectMemberDaoImpl implements ProjectMemberDao {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public Integer createProjectMember(ProjectMember projectMember) {

        return projectMemberMapper.createProjectMember(projectMember);
    }

    @Override
    public Integer deleteProjectMemberById(Integer id) {

        return projectMemberMapper.deleteProjectMemberById(id);
    }

    @Override
    public Integer updateProjectMember(ProjectMember projectMember) {

        return projectMemberMapper.updateProjectMember(projectMember);
    }

    @Override
    public ProjectMember getProjectMemberById(Integer id) {

        return projectMemberMapper.getProjectMemberById(id);
    }

    @Override
    public List<ProjectMember> getProjectsMembers() {

        return projectMemberMapper.getProjectsMembers();
    }
}
