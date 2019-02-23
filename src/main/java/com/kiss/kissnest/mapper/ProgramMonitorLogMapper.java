package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.ProgramMonitorLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProgramMonitorLogMapper {
    Integer createProgramMonitorLogMapper(ProgramMonitorLog programMonitorLog);
}
