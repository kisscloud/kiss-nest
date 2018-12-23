package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.mapper.EnvironmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EnvironmentDaoImpl implements EnvironmentDao {

    @Autowired
    private EnvironmentMapper environmentMapper;

    @Override
    public Integer createEnvironment(Environment environment) {

        return environmentMapper.createEnvironment(environment);
    }

    @Override
    public List<Environment> getEnvironmentsByTeamId(Integer teamId) {

        return environmentMapper.getEnvironmentsByTeamId(teamId);
    }

    @Override
    public Environment getEnvironmentById(Integer id) {

        return environmentMapper.getEnvironmentById(id);
    }

    @Override
    public Environment getEnvironmentByTeamIdAndName(Integer teamId, String name) {

        Map<String, Object> params = new HashMap<>();
        params.put("teamId", teamId);
        params.put("name", name);

        return environmentMapper.getEnvironmentByTeamIdAndName(params);
    }

    @Override
    public Integer updateEnvironment(Environment environment) {

        return environmentMapper.updateEnvironment(environment);
    }

    @Override
    public Integer addEnvironmentServerCount(Integer id) {

        return environmentMapper.addEnvironmentServerCount(id);
    }

    @Override
    public Integer deleteEnvironmentById(Integer id) {

        return environmentMapper.deleteEnvironmentById(id);
    }

    @Override
    public List<Environment> getEnvironmentsByProjectId(Integer projectId) {

        return environmentMapper.getEnvironmentsByProjectId(projectId);
    }
}
