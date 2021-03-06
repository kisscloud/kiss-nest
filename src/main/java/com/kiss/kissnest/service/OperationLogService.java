package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.kiss.kissnest.dao.DynamicDao;
import com.kiss.kissnest.dao.OperationLogDao;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.input.CreateTagInput;
import com.kiss.kissnest.output.DeployLogOutput;
import com.kiss.kissnest.output.DynamicOutput;
import com.kiss.kissnest.output.GetDynamicOutput;
import com.kiss.kissnest.output.OperationLogOutput;
import com.kiss.kissnest.util.LangUtil;
import com.kiss.foundation.entity.Guest;
import com.kiss.foundation.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OperationLogService {

    @Autowired
    private OperationLogDao operationLogDao;

    @Autowired
    private DynamicDao dynamicDao;

    @Autowired
    private LangUtil langUtil;

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
                operationLog.setAfterValue(after == null ? "" : JSON.toJSONString(after));
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

    public void saveDynamic(Guest guest, Integer teamId, Integer groupId, Integer projectId, Integer targetType, Object object) {

        Dynamic dynamic = new Dynamic();
        dynamic.setTeamId(teamId);
        dynamic.setGroupId(groupId);
        dynamic.setProjectId(projectId);
        dynamic.setTargetType(targetType);
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("operatorId", guest.getId());
        logMap.put("operatorName", guest.getName());
        logMap.put("targetType", targetType);
        if (targetType == 1) {
            Team team = (Team) object;
            logMap.put("name", team.getName());
            logMap.put("createdAt", team.getCreatedAt().getTime());
        } else if (targetType == 3) {
            Group group = (Group) object;
            logMap.put("name", group.getName());
            logMap.put("createdAt", group.getCreatedAt().getTime());
        } else if (targetType == OperationTargetType.TYPE_DELETE_GROUP) {
            Group group = (Group) object;
            logMap.put("name", group.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE_UPDATE_GROUP) {
            Group group = (Group) object;
            logMap.put("name", group.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == 6) {
            Project project = (Project) object;
            logMap.put("name", project.getName());
            logMap.put("createdAt", project.getCreatedAt().getTime());
        } else if (targetType == OperationTargetType.TYPE_DELETE_PROJECT) {
            Project project = (Project) object;
            logMap.put("name", project.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE_UPDATE_PROJECT) {
            Project project = (Project) object;
            logMap.put("name", project.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__CREATE_BUILD_JOB) {
            Job job = (Job) object;
            logMap.put("name", job.getJobName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__BUILD_JOB) {
            Job job = (Job) object;
            logMap.put("name", job.getJobName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__CREATE_ENVIRONMENT) {
            Environment environment = (Environment) object;
            logMap.put("name", environment.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__CREATE_SERVER) {
            Server server = (Server) object;
            logMap.put("name", server.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__UPDATE_SERVER) {
            Server server = (Server) object;
            logMap.put("name", server.getName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__CREATE_LINK) {
            Link link = (Link) object;
            logMap.put("name", link.getTitle());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__UPDATE_LINK) {
            Link link = (Link) object;
            logMap.put("name", link.getTitle());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__DELETE_LINK) {
            Link link = (Link) object;
            logMap.put("name", link.getTitle());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__CREATE_PROJECT_REPOSITORY) {
            ProjectRepository projectRepository = (ProjectRepository) object;
            logMap.put("name", projectRepository.getProjectName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == 18) {
            Track track = new Track();
            logMap.put("operatorName", track.getAuthorName() == null ? track.getAuthorEmail() : track.getAuthorName());
            logMap.put("name", track.getProjectName());
            logMap.put("version", track.getHash());
        } else if (targetType == OperationTargetType.TYPE__MERGE_REQUEST) {
            Track track = new Track();
            logMap.put("operatorName", track.getAuthorName() == null ? track.getAuthorEmail() : track.getAuthorName());
            logMap.put("name", track.getProjectName());
            logMap.put("version", track.getHash());
        } else if (targetType == OperationTargetType.TYPE__CREATE_DEPLOY_JOB) {
            Job job = (Job) object;
            logMap.put("name", job.getJobName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__DEPLOY_JOB) {
            DeployLogOutput deployLogOutput = (DeployLogOutput) object;
            logMap.put("projectName", deployLogOutput.getOperatorName());
            logMap.put("serverIds", deployLogOutput.getServerIds());
            logMap.put("envName", deployLogOutput.getEnvName());
            logMap.put("createdAt", new Date().getTime());
        } else if (targetType == OperationTargetType.TYPE__CREATE_PROJECT_TAG) {
            CreateTagInput createTagInput = (CreateTagInput) object;
            logMap.put("tagName", createTagInput.getTagName());
            logMap.put("ref", createTagInput.getRef());
            logMap.put("projectId", createTagInput.getProjectId());
        }

        dynamic.setLog(JSON.toJSONString(logMap));
        dynamic.setOperatorId(guest.getId());
        dynamic.setOperatorName(guest.getName());
        dynamicDao.createDynamic(dynamic);
    }

    public List<OperationLogOutput> getOperationLogsByTeamId(Integer teamId) {

        List<OperationLog> operationLogs = operationLogDao.getOperationLogsByTeamId(teamId);
        List<OperationLogOutput> operationLogOutputs = BeanCopyUtil.copyList(operationLogs, OperationLogOutput.class, BeanCopyUtil.defaultFieldNames);
        operationLogOutputs.forEach(operationLogOutput -> operationLogOutput.setTargetTypeText(langUtil.getEnumsMessage("operation.log", String.valueOf(operationLogOutput.getTargetType()))));

        return operationLogOutputs;
    }

    public GetDynamicOutput getDynamics(Integer teamId, Integer page, Integer size, Integer groupId, Integer projectId) {

        Integer start = page == null ? null : (page - 1) * size;
        Dynamic dynamic = new Dynamic();
        dynamic.setTeamId(teamId);
        dynamic.setGroupId(groupId);
        dynamic.setProjectId(projectId);
        List<Dynamic> dynamics = dynamicDao.getDynamics(teamId, start, size, groupId, projectId);
        List<DynamicOutput> dynamicOutputs = BeanCopyUtil.copyList(dynamics, DynamicOutput.class, BeanCopyUtil.defaultFieldNames);
        Integer count = dynamicDao.getDynamicsCount(dynamic);

        GetDynamicOutput getDynamicOutput = new GetDynamicOutput();
        getDynamicOutput.setDynamicOutputs(dynamicOutputs);
        getDynamicOutput.setCount(count);

        return getDynamicOutput;
    }
}
