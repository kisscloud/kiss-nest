package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMemberMapper {

    Integer createProjectMember(ProjectMember projectMember);

    Integer deleteProjectMemberById(Integer id);

    Integer updateProjectMember(ProjectMember projectMember);

    ProjectMember getProjectMemberById(Integer id);

    List<ProjectMember> getProjectsMembers();
}
