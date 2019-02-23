package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.ServerMonitorLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServerMonitorLogMapper {
    Integer createServerMonitorLog(ServerMonitorLog serverMonitorLog);
}
