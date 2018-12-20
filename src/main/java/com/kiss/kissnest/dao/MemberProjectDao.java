package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.MemberProject;

import java.util.List;

public interface MemberProjectDao {

    Integer createMemberProject(MemberProject memberProject);

    Integer createMemberProjects(List<MemberProject> memberProjects);

    List<MemberProject> getMemberProjects(Integer teamId, Integer projectId);

    MemberProject getMemberProject(Integer teamId, Integer projectId, Integer memberId);

    List<Member> getMemberProjectsByProjectId(Integer projectId);

    Integer deleteMemberProjectsByProjectId(Integer projectId);
}
