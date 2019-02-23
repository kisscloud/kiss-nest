package com.kiss.kissnest.dao.impl;

import com.kiss.kissnest.dao.ProgramMonitorLogDao;
import com.kiss.kissnest.entity.ProgramMonitorLog;
import com.kiss.kissnest.mapper.ProgramMonitorLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProgramMonitorLogDaoImpl implements ProgramMonitorLogDao {

    @Autowired
    private ProgramMonitorLogMapper programMonitorLogMapper;

    @Override
    public Integer createProgramMonitorLogMapper(ProgramMonitorLog programMonitorLog) {
        return programMonitorLogMapper.createProgramMonitorLogMapper(programMonitorLog);
    }
}
