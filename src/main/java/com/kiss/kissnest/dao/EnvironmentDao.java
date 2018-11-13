package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Environment;

import java.util.List;

public interface EnvironmentDao {

    Integer createEnvironment(Environment environment);

    List<Environment> getEnvironmentsByTeamId(Integer teamId);

    Environment getEnvironmentById(Integer id);
}
