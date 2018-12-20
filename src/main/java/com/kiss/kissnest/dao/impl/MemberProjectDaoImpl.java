package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.MemberProjectDao;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.MemberProject;
import com.kiss.kissnest.mapper.MemberProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberProjectDaoImpl implements MemberProjectDao {

    @Autowired
    private MemberProjectMapper memberProjectMapper;

    @Override
    public Integer createMemberProject(MemberProject memberProject) {

        return memberProjectMapper.createMemberProject(memberProject);
    }

    @Override
    public Integer createMemberProjects(List<MemberProject> memberProjects) {

        return memberProjectMapper.createMemberProjects(memberProjects);
    }

    @Override
    public List<MemberProject> getMemberProjects(Integer teamId, Integer projectId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("projectId", projectId);

        return memberProjectMapper.getMemberProjects(params);
    }

    @Override
    public MemberProject getMemberProject(Integer teamId, Integer projectId, Integer memberId) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("projectId", projectId);
        params.put("memberId", memberId);

        return memberProjectMapper.getMemberProject(params);
    }

    @Override
    public List<Member> getMemberProjectsByProjectId(Integer projectId) {

        return memberProjectMapper.getMemberProjectsByProjectId(projectId);
    }

    @Override
    public Integer deleteMemberProjectsByProjectId(Integer projectId) {

        return memberProjectMapper.deleteMemberProjectsByProjectId(projectId);
    }
}
