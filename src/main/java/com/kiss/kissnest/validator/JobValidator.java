package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ServerDao;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.Server;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.status.NestStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;


@Component
public class JobValidator implements Validator {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private ServerValidator serverValidator;

    @Autowired
    private JobDao jobDao;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(CreateJobInput.class) ||
                clazz.equals(BuildJobInput.class) ||
                clazz.equals(BuildLogsInput.class) ||
                clazz.equals(CreateDeployInput.class) ||
                clazz.equals(UpdateJobInput.class) ||
                clazz.equals(UpdateDeployInput.class) ||
                clazz.equals(DeployJobInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateJobInput.class.isInstance(target)) {
            CreateJobInput createJobInput = (CreateJobInput) target;
            validateProjectId(createJobInput.getProjectId(), errors);
            validateScript(createJobInput.getScript(), errors);
            validateType(createJobInput.getType(), errors);
        } else if (BuildJobInput.class.isInstance(target)) {
            BuildJobInput buildJobInput = (BuildJobInput) target;
            validateProjectId(buildJobInput.getProjectId(), errors);
            validateBranch(buildJobInput.getBranch(), errors);
            validateExecType(buildJobInput.getType(), errors);
        } else if (BuildLogsInput.class.isInstance(target)) {
            BuildLogsInput buildLogsInput = (BuildLogsInput) target;
            teamValidaor.validateId(buildLogsInput.getTeamId(), "teamId", errors);
            validatePage(buildLogsInput.getPage(), errors);
        } else if (UpdateJobInput.class.isInstance(target)) {
            UpdateJobInput updateJobInput = (UpdateJobInput) target;
            validateJobId(updateJobInput.getId(), errors);
            teamValidaor.validateId(updateJobInput.getTeamId(), "teamId", errors);
            validateProjectId(updateJobInput.getProjectId(), errors);
            validateType(updateJobInput.getType(), errors);
            validateScript(updateJobInput.getScript(), errors);
        } else if (CreateDeployInput.class.isInstance(target)) {
            CreateDeployInput createDeployInput = (CreateDeployInput) target;
            teamValidaor.validateId(createDeployInput.getTeamId(), "teamId", errors);
            validateProjectId(createDeployInput.getProjectId(), errors);
            boolean envIdVal = serverValidator.validateEnvId(createDeployInput.getEnvId(), errors);

            if (envIdVal) {
                validateServerIds(createDeployInput.getServerIds(), createDeployInput.getEnvId(), errors);
            }

            validateConf(createDeployInput.getConf(), errors);
            validateType(createDeployInput.getType(), errors);
            validateScript(createDeployInput.getScript(), errors);
        } else if (UpdateDeployInput.class.isInstance(target)) {
            UpdateDeployInput updateDeployInput = (UpdateDeployInput) target;
            validateJobId(updateDeployInput.getId(), errors);
            boolean envIdVal = serverValidator.validateEnvId(updateDeployInput.getEnvId(), errors);

            if (envIdVal) {
                validateServerIds(updateDeployInput.getServerIds(), updateDeployInput.getEnvId(), errors);
            }

            validateConf(updateDeployInput.getConf(), errors);
            validateType(updateDeployInput.getType(), errors);
        } else if (DeployJobInput.class.isInstance(target)) {

        }
    }

    public void validateProjectId(Integer projectId, Errors errors) {

        if (projectId == null) {
            errors.rejectValue("projectId", String.valueOf(NestStatusCode.PROJECT_ID_IS_EMPTY), "项目id不能为空");
            return;
        }

        Project project = projectDao.getProjectById(projectId);

        if (project == null) {
            errors.rejectValue("projectId", String.valueOf(NestStatusCode.PROJECT_NOT_EXIST), "项目不存在");
        }
    }

    public void validateScript(String shell, Errors errors) {

        if (StringUtils.isEmpty(shell)) {
            errors.rejectValue("shell", String.valueOf(NestStatusCode.SHELL_IS_EMPTY));
        }
    }

    public void validateType(Integer type, Errors errors) {

        if (type == null) {
            errors.rejectValue("type", String.valueOf(NestStatusCode.TYPE_IS_EMPTY), "job类型不能为空");
        }
    }

    public void validateBranch(String branch, Errors errors) {

        if (StringUtils.isEmpty(branch)) {
            errors.rejectValue("branch", String.valueOf(NestStatusCode.BRANCH_IS_EMPTY), "分支名不能为空");
        }
    }

    public void validatePage(Integer page, Errors errors) {

        if (page == null) {
            errors.rejectValue("page", String.valueOf(NestStatusCode.PAGE_IS_EMPTY));
        }

        if (page < 0) {
            errors.rejectValue("page", String.valueOf(NestStatusCode.PAGE_IS_ERROR));
        }
    }

    public void validateSize(Integer size, Errors errors) {

        if (size == null) {
            errors.rejectValue("size", String.valueOf(NestStatusCode.SIZE_IS_EMPTY));
        }

        if (size < 0) {
            errors.rejectValue("size", String.valueOf(NestStatusCode.SIZE_IS_ERROR));
        }
    }

    public void validateJobId(Integer id, Errors errors) {

        if (id == null) {
            errors.rejectValue("id", String.valueOf(NestStatusCode.JOB_ID_IS_EMPTY), "任务id为空");
            return;
        }

        Job job = jobDao.getJobById(id);

        if (job == null) {
            errors.rejectValue("id", String.valueOf(NestStatusCode.JOB_NOT_EXIST), "任务不存在");
        }
    }

    public void validateExecType(Integer type, Errors errors) {

        if (type == null) {
            errors.rejectValue("type", String.valueOf(NestStatusCode.EXECJOB_TYPE_IS_EMPTY), "版本类型为空");
            return;
        }

        if (type != 0 && type != 1) {
            errors.rejectValue("type", String.valueOf(NestStatusCode.EXECJOB_TYPE_ERROR), "版本类型错误");
        }

    }

    public void validateServerIds(List<Integer> serverIds, Integer envId, Errors errors) {

        if (serverIds == null || serverIds.size() == 0) {
            errors.rejectValue("serverIds", String.valueOf(NestStatusCode.SERVERS_IS_EMPTY), "服务器列表为空");
            return;
        }

        serverIds.forEach(id -> {
            Server server = serverDao.getServerById(id);

            if (server == null) {
                errors.rejectValue("serverIds", String.valueOf(NestStatusCode.SERVER_NOT_EXIST), "服务器不存在");
                return;
            }

            if (envId != server.getEnvId()) {
                errors.rejectValue("serverIds", String.valueOf(NestStatusCode.SERVERS_ENVID_NOT_MATCH), "服务器与环境不匹配");
                return;
            }
        });
    }

    public void validateConf(String conf, Errors errors) {

        if (StringUtils.isEmpty(conf)) {
            errors.rejectValue("conf", String.valueOf(NestStatusCode.DEPLOY_JOB_CONF_IS_EMPTY), "部署任务的配置为空");
        }
    }
}
