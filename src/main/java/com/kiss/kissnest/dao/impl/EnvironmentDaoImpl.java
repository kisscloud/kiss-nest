package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.mapper.EnvironmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
