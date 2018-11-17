package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Environment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface EnvironmentMapper {

    Integer createEnvironment(Environment environment);

    List<Environment> getEnvironmentsByTeamId(Integer teamId);

    Environment getEnvironmentById(Integer id);

    Environment getEnvironmentByTeamIdAndName(Map params);

    Integer updateEnvironment(Environment environment);

    Integer addEnvironmentServerCount(Integer id);
}
