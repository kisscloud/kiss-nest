package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.input.CreateEnvironmentInput;
import com.kiss.kissnest.input.CreateServerInput;
import com.kiss.kissnest.input.UpdateEnvironmentInput;
import com.kiss.kissnest.input.UpdateServerInput;
import com.kiss.kissnest.output.EnvironmentOutput;
import com.kiss.kissnest.output.GetServerOutput;
import com.kiss.kissnest.output.ServerOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.LangUtil;
import entity.Guest;
import exception.StatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private JobDao jobDao;

    @Autowired
    private LangUtil langUtil;

    @Value("${server.maxSize}")
    private String serverSize;

    @Transactional
    public EnvironmentOutput createEnvironment(CreateEnvironmentInput environmentInput) {

        Environment environment = BeanCopyUtil.copy(environmentInput, Environment.class);
        Guest guest = ThreadLocalUtil.getGuest();
        environment.setOperatorId(guest.getId());
        environment.setOperatorName(guest.getName());
        environment.setServerCount(0);
        Integer count = environmentDao.createEnvironment(environment);

        if (count == 0) {
            throw new StatusException(NestStatusCode.SERVER_ENVIRONMENT_CREATE_FAILED);
        }

        EnvironmentOutput environmentOutput = BeanCopyUtil.copy(environment, EnvironmentOutput.class, BeanCopyUtil.defaultFieldNames);
        operationLogService.saveOperationLog(environmentInput.getTeamId(), guest, null, environment, "id", OperationTargetType.TYPE__CREATE_ENVIRONMENT);
        operationLogService.saveDynamic(guest, environment.getTeamId(), null, null, OperationTargetType.TYPE__CREATE_ENVIRONMENT, environment);
        environmentOutput.setTypeText(langUtil.getEnumsMessage("environment.type", String.valueOf(environmentOutput.getType())));

        return environmentOutput;
    }

    @Transactional
    public EnvironmentOutput updateEnvironment(UpdateEnvironmentInput updateEnvironmentInput) {

        Environment oldValue = environmentDao.getEnvironmentById(updateEnvironmentInput.getId());
        Environment environment = BeanCopyUtil.copy(updateEnvironmentInput, Environment.class);
        Guest guest = ThreadLocalUtil.getGuest();
        environment.setOperatorName(guest.getName());
        environment.setOperatorId(guest.getId());
        Integer count = environmentDao.updateEnvironment(environment);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_SERVER_ENVIRONMENT_FAILED);
        }

        Environment newValue = environmentDao.getEnvironmentById(environment.getId());
        EnvironmentOutput environmentOutput = BeanCopyUtil.copy(newValue, EnvironmentOutput.class, BeanCopyUtil.defaultFieldNames);
        environmentOutput.setTypeText(langUtil.getEnumsMessage("environment.type", String.valueOf(environmentOutput.getType())));
        operationLogService.saveOperationLog(updateEnvironmentInput.getTeamId(), guest, oldValue, newValue, "id", OperationTargetType.TYPE__CREATE_ENVIRONMENT);
        operationLogService.saveDynamic(guest, environment.getTeamId(), null, null, OperationTargetType.TYPE__CREATE_ENVIRONMENT, environment);

        return environmentOutput;
    }

    public List<EnvironmentOutput> getEnvironmentsByTeamId(Integer teamId) {

        List<Environment> environment = environmentDao.getEnvironmentsByTeamId(teamId);
        List<EnvironmentOutput> environmentOutputs = BeanCopyUtil.copyList(environment, EnvironmentOutput.class, BeanCopyUtil.defaultFieldNames);
        environmentOutputs.forEach(environmentOutput -> environmentOutput.setTypeText(langUtil.getEnumsMessage("environment.type", String.valueOf(environmentOutput.getType()))));

        return environmentOutputs;
    }

    public ServerOutput createServer(CreateServerInput createServerInput) {

        Server server = BeanCopyUtil.copy(createServerInput, Server.class);
        Guest guest = ThreadLocalUtil.getGuest();
        server.setOperatorId(guest.getId());
        server.setOperatorName(guest.getName());
        Integer count = serverDao.createServer(server);

        if (count == 0) {
            throw new StatusException(NestStatusCode.SERVER_CREATE_FAILED);
        }

        environmentDao.addEnvironmentServerCount(createServerInput.getEnvId());
        Environment environment = environmentDao.getEnvironmentById(createServerInput.getEnvId());
        ServerOutput serverOutput = BeanCopyUtil.copy(server, ServerOutput.class, BeanCopyUtil.defaultFieldNames);
        serverOutput.setEnvName(environment.getName());
        operationLogService.saveOperationLog(createServerInput.getTeamId(), guest, null, server, "id", OperationTargetType.TYPE__CREATE_SERVER);
        operationLogService.saveDynamic(guest, server.getTeamId(), null, null, OperationTargetType.TYPE__CREATE_SERVER, server);
        return serverOutput;
    }

    public ServerOutput updateServer(UpdateServerInput updateServerInput) {

        Server server = BeanCopyUtil.copy(updateServerInput, Server.class);
        Guest guest = ThreadLocalUtil.getGuest();
        server.setOperatorId(guest.getId());
        server.setOperatorName(guest.getName());
        Server oldValue = serverDao.getServerById(updateServerInput.getId());
        Integer count = serverDao.updateServer(server);

        if (count == 0) {
            throw new StatusException(NestStatusCode.SERVER_UPDATE_FAILED);
        }

        ServerOutput serverOutput = BeanCopyUtil.copy(server, ServerOutput.class, BeanCopyUtil.defaultFieldNames);
        operationLogService.saveOperationLog(updateServerInput.getTeamId(), guest, oldValue, server, "id", OperationTargetType.TYPE__UPDATE_SERVER);
        operationLogService.saveDynamic(guest, server.getTeamId(), null, null, OperationTargetType.TYPE__UPDATE_SERVER, server);
        return serverOutput;
    }

    public List<ServerOutput> getServersByTeamId(Integer teamId, Integer page, Integer size, Integer envId) {

        Integer maxSize = Integer.parseInt(serverSize);
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = page == 0 ? null : (page - 1) * pageSize;
        List<Server> servers = serverDao.getServersByTeamId(teamId, start, pageSize, envId);
        List<ServerOutput> serverOutputs = BeanCopyUtil.copyList(servers, ServerOutput.class, BeanCopyUtil.defaultFieldNames);

        return serverOutputs;
    }

    public GetServerOutput getServerOutputByTeamId(Integer teamId, Integer page, Integer size, Integer envId) {

        Integer maxSize = Integer.parseInt(serverSize);
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = page == 0 ? null : (page - 1) * pageSize;
        List<ServerOutput> serverOutputs = serverDao.getServerOutputsByTeamId(teamId, start, pageSize, envId);
        Integer count = serverDao.getServerOutputCount(teamId, envId);

        serverOutputs.forEach(serverOutput -> {
            String projectName = projectDao.getProjectNameByServerId("%" + serverOutput.getId() + "%");
            serverOutput.setProjectName(projectName);
            serverOutput.setStatusText(langUtil.getEnumsMessage("server.status", String.valueOf(serverOutput.getStatus())));
        });

        GetServerOutput getServerOutput = new GetServerOutput();
        getServerOutput.setCount(count);
        getServerOutput.setServerOutputs(serverOutputs);

        return getServerOutput;
    }

    public void deleteEnvironmentById(Integer id) {

        List<Server> servers = serverDao.getServersByEnvId(id);

        if (servers != null && servers.size() != 0) {
            throw new StatusException(NestStatusCode.SERVER_ENVIRONMENT_HAS_SERVERS);
        }

        Environment environment = environmentDao.getEnvironmentById(id);

        if (environment == null) {
            throw new StatusException(NestStatusCode.SERVER_ENVIRONMENT_NOT_EXIST);
        }

        Integer count = environmentDao.deleteEnvironmentById(id);

        if (count == 0) {
            throw new StatusException(NestStatusCode.DELETE_SERVER_ENVIRONMENT_FAILED);
        }

    }

    public void deleteServerById(Integer id) {

        Server server = serverDao.getServerById(id);

        if (server == null) {
            throw new StatusException(NestStatusCode.SERVER_NOT_EXIST);
        }

        List<Job> jobs = jobDao.getJobsByServerIds(id);

        if (jobs != null && jobs.size() != 0) {
            throw new StatusException(NestStatusCode.SERVER_HAS_JOB);
        }

        Integer count = serverDao.deleteServerById(id);

        if (count == 0) {
            throw new StatusException(NestStatusCode.DELETE_SERVER_FAILED);
        }
    }
}
