package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.OperateLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperateLogMapper {

    Integer createOperateLog(OperateLog operateLog);

    List<OperateLog> getOperateLogsByTeamId(Integer teamId);
}
