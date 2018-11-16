package com.kiss.kissnest.mapper;

import com.kiss.kissnest.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    Integer createOperationLog(OperationLog operateLog);

    List<OperationLog> getOperationLogsByTeamId(Integer teamId);
}
