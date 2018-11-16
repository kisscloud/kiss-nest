package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.kiss.kissnest.dao.OperationLogDao;
import com.kiss.kissnest.entity.OperationLog;
import com.kiss.kissnest.output.OperationLogOutput;
import com.kiss.kissnest.util.CodeUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import output.ResultOutput;
import utils.BeanCopyUtil;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

@Component
public class OperationLogService {

    @Autowired
    private OperationLogDao operationLogDao;

    @Autowired
    private CodeUtil codeUtil;

    public void saveOperationLog(Integer teamId, Guest guest, Object before, Object after, String targetField, Integer targetType) {

        OperationLog operationLog = new OperationLog();

        try {
            if (before == null) {
                Field field = after.getClass().getDeclaredField(targetField);
                field.setAccessible(true);
                operationLog.setTargetId(Integer.parseInt(field.get(after).toString()));
                operationLog.setBeforeValue("");
                operationLog.setAfterValue(JSON.toJSONString(after));
            } else {
                Field field = before.getClass().getDeclaredField(targetField);
                field.setAccessible(true);
                operationLog.setTargetId(Integer.parseInt(field.get(before).toString()));
                operationLog.setBeforeValue(JSON.toJSONString(before));
                operationLog.setAfterValue("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        operationLog.setTargetType(targetType);
        operationLog.setTeamId(teamId);
        saveLog(guest, operationLog);
    }

    private void saveLog(Guest guest, OperationLog operationLog) {
        operationLog.setOperatorId(guest.getId());
        operationLog.setOperatorName(guest.getName());
        operationLog.setCreatedAt(new Date());
        operationLog.setUpdatedAt(new Date());
        operationLog.setRecoveredAt(null);
        operationLogDao.createOperationLog(operationLog);
    }

    public ResultOutput getOperationLogsByTeamId(Integer teamId) {

        List<OperationLog> operationLogs = operationLogDao.getOperationLogsByTeamId(teamId);
        List<OperationLogOutput> operationLogOutputs = (List) BeanCopyUtil.copyList(operationLogs,OperationLogOutput.class,BeanCopyUtil.defaultFieldNames);
        operationLogOutputs.forEach(operationLogOutput -> operationLogOutput.setTargetTypeText(codeUtil.getEnumsMessage("operation.log",String.valueOf(operationLogOutput.getTargetType()))));

        return ResultOutputUtil.success(operationLogOutputs);
    }
}
