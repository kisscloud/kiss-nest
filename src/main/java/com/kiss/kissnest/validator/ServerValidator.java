package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.EnvironmentDao;
import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Environment;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.input.CreateEnvironmentInput;
import com.kiss.kissnest.input.CreateServerInput;
import com.kiss.kissnest.input.UpdateEnvironmentInput;
import com.kiss.kissnest.input.UpdateServerInput;
import com.kiss.kissnest.status.NestStatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ServerValidator implements Validator {

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private EnvironmentDao environmentDao;

    @Autowired
    private ServerDao serverDao;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(CreateEnvironmentInput.class) ||
                clazz.equals(CreateServerInput.class) ||
                clazz.equals(UpdateServerInput.class) ||
                clazz.equals(UpdateEnvironmentInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateEnvironmentInput.class.isInstance(target)) {
            CreateEnvironmentInput environmentInput = (CreateEnvironmentInput) target;
            boolean teamVal = teamValidaor.validateId(environmentInput.getTeamId(),"teamId",errors);

            if (teamVal) {
                validateCreateEnvironmentName(environmentInput.getTeamId(),environmentInput.getName(),errors);
            }

            validateType(environmentInput.getType(),errors);
        } else if (CreateServerInput.class.isInstance(target)) {
            CreateServerInput serverInput = (CreateServerInput) target;
            boolean teamVal = teamValidaor.validateId(serverInput.getTeamId(),"teamId",errors);

            if (teamVal) {
                validateServerName(serverInput.getName(),serverInput.getTeamId(),errors);
            }

            validateEnvId(serverInput.getEnvId(),errors);
            validateInnerIp(serverInput.getInnerIp(),errors);
        } else if (UpdateServerInput.class.isInstance(target)) {
            UpdateServerInput updateServerInput = (UpdateServerInput) target;
            boolean teamVal = teamValidaor.validateId(updateServerInput.getTeamId(),"teamId",errors);
            boolean idVal = validateId(updateServerInput.getId(),errors);

            if (teamVal && idVal) {
                validateServerName(updateServerInput.getName(),updateServerInput.getTeamId(),updateServerInput.getId(),errors);
            }

            validateEnvId(updateServerInput.getEnvId(),errors);
            validateInnerIp(updateServerInput.getInnerIp(),errors);
        } else if (UpdateEnvironmentInput.class.isInstance(target)) {
            UpdateEnvironmentInput updateEnvironmentInput = (UpdateEnvironmentInput) target;
            boolean envIdVal = validateEnvId(updateEnvironmentInput.getEnvId(),errors);
            boolean teamVal = teamValidaor.validateId(updateEnvironmentInput.getTeamId(),"teamId",errors);

            if (teamVal && envIdVal) {
                validateUpdateEnvironmentName(updateEnvironmentInput.getTeamId(),updateEnvironmentInput.getEnvId(),updateEnvironmentInput.getName(),errors);
            }
        }
    }

    public void validateCreateEnvironmentName(Integer teamId,String name,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_ENVIRONMENT_NAME_IS_EMPTY),"环境名称不能为空");
        }

        Environment environment = environmentDao.getEnvironmentByTeamIdAndName(teamId,name);

        if (environment != null) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_ENVIRONMENT_NAME_IS_EXIST));
        }
    }

    public void validateType(Integer type,Errors errors) {

        if (type == null) {
            errors.rejectValue("type",String.valueOf(NestStatusCode.SERVER_ENVIRONMENT_TYPE_IS_EMPTY));
        }
    }

    public void validateServerName(String name,Integer teamId,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_SERVER_NAME_IS_EMPTY),"服务器名称不能为空");
        }

        Server server = serverDao.getServerByNameAndTeamId(teamId,name);

        if (server != null) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_SERVER_NAME_EXIST),"服务器名称已存在");
        }
    }

    public void validateServerName(String name,Integer teamId,Integer id,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_SERVER_NAME_IS_EMPTY),"服务器名称不能为空");
        }

        Server server = serverDao.getServerByNameAndTeamId(teamId,name);

        if (server != null && server.getId() != id) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_SERVER_NAME_EXIST),"服务器名称已存在");
        }
    }

    public boolean validateEnvId(Integer envId,Errors errors) {

        if (envId == null) {
            errors.rejectValue("envId",String.valueOf(NestStatusCode.SERVER_ENVID_IS_EMPTY));
            return false;
        }

        Environment environment = environmentDao.getEnvironmentById(envId);

        if (environment == null) {
            errors.rejectValue("envId",String.valueOf(NestStatusCode.SERVER_ENVID_NOT_EXIST));
            return false;
        }

        return true;
    }

    public void validateInnerIp(String innerIp,Errors errors) {

        if (innerIp == null) {
            errors.rejectValue("innerIp",String.valueOf(NestStatusCode.SERVER_INNERIP_IS_EMPTY));
        }
    }

    public boolean validateId(Integer id,Errors errors) {

        if (id == null) {
            errors.rejectValue("id",String.valueOf(NestStatusCode.SERVERID_IS_EMPTY),"服务器id不能为空");
            return false;
        }

        Server server = serverDao.getServerById(id);

        if (server == null) {
            errors.rejectValue("id",String.valueOf(NestStatusCode.SERVER_NOT_EXIST),"服务器不存在");
            return false;
        }

        return true;
    }

    public void validateUpdateEnvironmentName(Integer teamId,Integer envId,String name,Errors errors) {

        if (StringUtils.isEmpty(name)) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_ENVIRONMENT_NAME_IS_EMPTY));
        }

        Environment environment = environmentDao.getEnvironmentByTeamIdAndName(teamId,name);

        if (environment != null && !envId.equals(environment.getId())) {
            errors.rejectValue("name",String.valueOf(NestStatusCode.SERVER_ENVIRONMENT_NAME_IS_EXIST));
        }
    }
}
