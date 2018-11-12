package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.Environment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EnvironmentMapper {

    Integer createEnvironment(Environment environment);

    List<Environment> getEnvironmentsByTeamId(Integer teamId);

    Environment getEnvironmentById(Integer id);
}
