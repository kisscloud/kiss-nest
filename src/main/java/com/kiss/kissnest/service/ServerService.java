package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.entity.OperationTargetType;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.input.EnvironmentInput;
import com.kiss.kissnest.input.CreateServerInput;
import com.kiss.kissnest.input.UpdateServerInput;
import com.kiss.kissnest.output.EnvironmentOutput;
import com.kiss.kissnest.output.ServerOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.ResultOutputUtil;
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
    private OperationLogService operationLogService;

    @Value("${server.maxSize}")
    private String serverSize;

    public ResultOutput createEnvironment(EnvironmentInput environmentInput) {

        Environment environment = (Environment) BeanCopyUtil.copy(environmentInput, Environment.class);
        Guest guest = ThreadLocalUtil.getGuest();
        environment.setOperatorId(guest.getId());
        environment.setOperatorName(guest.getName());
        Integer count = environmentDao.createEnvironment(environment);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.SERVER_ENVIRONMENT_CREATE_FAILED);
        }

        EnvironmentOutput environmentOutput = (EnvironmentOutput) BeanCopyUtil.copy(environment,EnvironmentOutput.class,BeanCopyUtil.defaultFieldNames);
//        operationLogService.saveOperationLog(environmentInput.getTeamId(),guest,null,environment,"id",OperationTargetType.TYPE__CREATE_ENVIRONMENT);

        return ResultOutputUtil.success(environmentOutput);
    }

    public ResultOutput getEnvironmentByTeamId(Integer teamId) {

        List<Environment> environment = environmentDao.getEnvironmentsByTeamId(teamId);

        List<EnvironmentOutput> environmentOutputs = (List) BeanCopyUtil.copyList(environment,EnvironmentOutput.class,BeanCopyUtil.defaultFieldNames);

        return ResultOutputUtil.success(environmentOutputs);
    }

    public ResultOutput createServer(CreateServerInput createServerInput) {

        Server server = (Server) BeanCopyUtil.copy(createServerInput,Server.class);
        Guest guest = ThreadLocalUtil.getGuest();
        server.setOperatorId(guest.getId());
        server.setOperatorName(guest.getName());
        Integer count = serverDao.createServer(server);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.SERVER_CREATE_FAILED);
        }

        ServerOutput serverOutput = (ServerOutput) BeanCopyUtil.copy(server,ServerOutput.class,BeanCopyUtil.defaultFieldNames);
//        operationLogService.saveOperationLog(createServerInput.getTeamId(),guest,null,server,"id",OperationTargetType.TYPE__CREATE_SERVER);

        return ResultOutputUtil.success(serverOutput);
    }

    public ResultOutput updateServer(UpdateServerInput updateServerInput) {

        Server server = (Server) BeanCopyUtil.copy(updateServerInput,Server.class);
        Guest guest = ThreadLocalUtil.getGuest();
        server.setOperatorId(guest.getId());
        server.setOperatorName(guest.getName());
        Server oldValue = serverDao.getServerById(updateServerInput.getId());
        Integer count = serverDao.updateServer(server);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.SERVER_UPDATE_FAILED);
        }

        ServerOutput serverOutput = (ServerOutput) BeanCopyUtil.copy(server,ServerOutput.class,BeanCopyUtil.defaultFieldNames);
//        operationLogService.saveOperationLog(updateServerInput.getTeamId(),guest,oldValue,server,"id",OperationTargetType.TYPE__UPDATE_SERVER);

        return ResultOutputUtil.success(serverOutput);
    }

    public ResultOutput getServersByTeamId(Integer teamId,Integer page,Integer size) {

        Integer maxSize = Integer.parseInt(serverSize);
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        List<Server> servers = serverDao.getServersByTeamId(teamId,(page - 1) * pageSize,pageSize);
        List<ServerOutput> serverOutputs = (List) BeanCopyUtil.copyList(servers,ServerOutput.class,BeanCopyUtil.defaultFieldNames);

        return ResultOutputUtil.success(serverOutputs);
    }

    public ResultOutput getServersByEnvironment(Integer teamId,Integer envId){

        List<Server> servers = serverDao.getServersByEnvironment(teamId,envId);
        List<ServerOutput> serverOutputs = (List) BeanCopyUtil.copyList(servers,ServerOutput.class,BeanCopyUtil.defaultFieldNames);

        return ResultOutputUtil.success(serverOutputs);
    }
}
