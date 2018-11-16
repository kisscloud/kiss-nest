package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperateLogMapper {

    Integer createOperateLog(OperationLog operateLog);

    List<OperationLog> getOperateLogsByTeamId(Integer teamId);
}
