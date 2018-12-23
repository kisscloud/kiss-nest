package com.kiss.kissnest.dao;

import com.kiss.kissnest.entity.Environment;

import java.util.List;
import java.util.Map;

public interface EnvironmentDao {

    Integer createEnvironment(Environment environment);

    List<Environment> getEnvironmentsByTeamId(Integer teamId);

    Environment getEnvironmentById(Integer id);

    Environment getEnvironmentByTeamIdAndName(Integer teamId, String name);

    Integer updateEnvironment(Environment environment);

    Integer addEnvironmentServerCount(Integer id);

    Integer deleteEnvironmentById(Integer id);

    List<Environment> getEnvironmentsByProjectId(Integer projectId);
}
