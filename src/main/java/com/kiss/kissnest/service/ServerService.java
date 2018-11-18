package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.input.CreateEnvironmentInput;
import com.kiss.kissnest.input.CreateServerInput;
import com.kiss.kissnest.input.UpdateEnvironmentInput;
import com.kiss.kissnest.input.UpdateServerInput;
import com.kiss.kissnest.output.EnvironmentOutput;
import com.kiss.kissnest.output.GetServerOutput;
import com.kiss.kissnest.output.ServerOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.CodeUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import com.sun.org.apache.regexp.internal.RE;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.BeanCopyUtil;
import utils.ThreadLocalUtil;

import java.util.List;

@Service
public class ServerService {

    @Autowired
    private EnvironmentDao environmentDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private CodeUtil codeUtil;

    @Value("${server.maxSize}")
    private String serverSize;

    public ResultOutput createEnvironment(CreateEnvironmentInput environmentInput) {

        Environment environment = BeanCopyUtil.copy(environmentInput, Environment.class);
        Guest guest = ThreadLocalUtil.getGuest();
        environment.setOperatorId(guest.getId());
        environment.setOperatorName(guest.getName());
        environment.setServerCount(0);
        Integer count = environmentDao.createEnvironment(environment);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.SERVER_ENVIRONMENT_CREATE_FAILED);
        }

        EnvironmentOutput environmentOutput = BeanCopyUtil.copy(environment, EnvironmentOutput.class, BeanCopyUtil.defaultFieldNames);
//        operationLogService.saveOperationLog(environmentInput.getTeamId(),guest,null,environment,"id",OperationTargetType.TYPE__CREATE_ENVIRONMENT);
        environmentOutput.setTypeText(codeUtil.getEnumsMessage("environment.type",String.valueOf(environmentOutput.getType())));

        return ResultOutputUtil.success(environmentOutput);
    }

    public ResultOutput updateEnvironment(UpdateEnvironmentInput updateEnvironmentInput) {

        Environment environment = BeanCopyUtil.copy(updateEnvironmentInput,Environment.class);
        environment.setId(updateEnvironmentInput.getId());
        Guest guest = ThreadLocalUtil.getGuest();
        environment.setOperatorId(guest.getId());
        environment.setOperatorName(guest.getName());
        Integer count = environmentDao.updateEnvironment(environment);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_SERVER_ENVIRONMENT_FAILED);
        }

        EnvironmentOutput environmentOutput = BeanCopyUtil.copy(environment,EnvironmentOutput.class);
        environmentOutput.setTypeText(codeUtil.getEnumsMessage("environment.type",String.valueOf(environmentOutput.getType())));

        return ResultOutputUtil.success(environmentOutput);
    }

    public ResultOutput getEnvironmentsByTeamId(Integer teamId) {

        List<Environment> environment = environmentDao.getEnvironmentsByTeamId(teamId);
        List<EnvironmentOutput> environmentOutputs = BeanCopyUtil.copyList(environment, EnvironmentOutput.class, BeanCopyUtil.defaultFieldNames);
        environmentOutputs.forEach(environmentOutput -> environmentOutput.setTypeText(codeUtil.getEnumsMessage("environment.type",String.valueOf(environmentOutput.getType()))));

        return ResultOutputUtil.success(environmentOutputs);
    }

    public ResultOutput createServer(CreateServerInput createServerInput) {

        Server server = BeanCopyUtil.copy(createServerInput, Server.class);
        Guest guest = ThreadLocalUtil.getGuest();
        server.setOperatorId(guest.getId());
        server.setOperatorName(guest.getName());
        Integer count = serverDao.createServer(server);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.SERVER_CREATE_FAILED);
        }

        environmentDao.addEnvironmentServerCount(createServerInput.getEnvId());
        ServerOutput serverOutput = BeanCopyUtil.copy(server, ServerOutput.class, BeanCopyUtil.defaultFieldNames);
//        operationLogService.saveOperationLog(createServerInput.getTeamId(),guest,null,server,"id",OperationTargetType.TYPE__CREATE_SERVER);
        return ResultOutputUtil.success(serverOutput);
    }

    public ResultOutput updateServer(UpdateServerInput updateServerInput) {

        Server server = BeanCopyUtil.copy(updateServerInput, Server.class);
        Guest guest = ThreadLocalUtil.getGuest();
        server.setOperatorId(guest.getId());
        server.setOperatorName(guest.getName());
        Server oldValue = serverDao.getServerById(updateServerInput.getId());
        Integer count = serverDao.updateServer(server);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.SERVER_UPDATE_FAILED);
        }

        ServerOutput serverOutput = BeanCopyUtil.copy(server, ServerOutput.class, BeanCopyUtil.defaultFieldNames);
//        operationLogService.saveOperationLog(updateServerInput.getTeamId(),guest,oldValue,server,"id",OperationTargetType.TYPE__UPDATE_SERVER);

        return ResultOutputUtil.success(serverOutput);
    }

    public ResultOutput getServersByTeamId(Integer teamId, Integer page, Integer size, Integer envId) {

        Integer maxSize = Integer.parseInt(serverSize);
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = page == 0 ? null : (page - 1) * pageSize;
        List<Server> servers = serverDao.getServersByTeamId(teamId, start, pageSize, envId);
        List<ServerOutput> serverOutputs = BeanCopyUtil.copyList(servers, ServerOutput.class, BeanCopyUtil.defaultFieldNames);

        return ResultOutputUtil.success(serverOutputs);
    }

    public ResultOutput getServerOutputByTeamId(Integer teamId, Integer page, Integer size, Integer envId) {

        Integer maxSize = Integer.parseInt(serverSize);
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = page == 0 ? null : (page - 1) * pageSize;
        List<ServerOutput> serverOutputs = serverDao.getServerOutputsByTeamId(teamId, start, pageSize, envId);
        Integer count = serverDao.getServerOutputCount(teamId,envId);

        serverOutputs.forEach(serverOutput -> {
            String projectName = projectDao.getProjectNameByServerId("%" + serverOutput.getId() + "%");
            serverOutput.setProjectName(projectName);
            serverOutput.setStatusText(codeUtil.getEnumsMessage("server.status",String.valueOf(serverOutput.getStatus())));
        });

        GetServerOutput getServerOutput = new GetServerOutput();
        getServerOutput.setCount(count);
        getServerOutput.setServerOutputs(serverOutputs);

        return ResultOutputUtil.success(getServerOutput);
    }
}
