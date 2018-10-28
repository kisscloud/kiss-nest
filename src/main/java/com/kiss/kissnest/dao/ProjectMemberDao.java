package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.ProjectMember;

import java.util.List;

public interface ProjectMemberDao {

    Integer createProjectMember(ProjectMember projectMember);

    Integer deleteProjectMemberById(Integer id);

    Integer updateProjectMember(ProjectMember projectMember);

    ProjectMember getProjectMemberById(Integer id);

    List<ProjectMember> getProjectsMembers();
}
