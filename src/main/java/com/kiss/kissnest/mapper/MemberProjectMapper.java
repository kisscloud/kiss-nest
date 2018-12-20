package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.MemberProject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MemberProjectMapper {

    Integer createMemberProject(MemberProject memberProject);

    Integer createMemberProjects(List<MemberProject> memberProjects);

    List<MemberProject> getMemberProjects(Map params);

    MemberProject getMemberProject(Map params);

    List<Member> getMemberProjectsByProjectId(Integer projectId);

    Integer deleteMemberProjectsByProjectId(Integer projectId);
}
