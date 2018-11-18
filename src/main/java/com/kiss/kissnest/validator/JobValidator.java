package com.kiss.kissnest.validator;

import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.BuildJobInput;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.input.BuildLogsInput;
import com.kiss.kissnest.input.UpdateJobInput;
import com.kiss.kissnest.status.NestStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class JobValidator implements Validator {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TeamValidaor teamValidaor;

    @Autowired
    private JobDao jobDao;

    @Override
    public boolean supports(Class<?> clazz) {

        return clazz.equals(CreateJobInput.class) ||
                clazz.equals(BuildJobInput.class) ||
                clazz.equals(BuildLogsInput.class) ||
                clazz.equals(UpdateJobInput.class);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (CreateJobInput.class.isInstance(target)) {
            CreateJobInput createJobInput = (CreateJobInput) target;
            validateProjectId(createJobInput.getProjectId(),errors);
            validateScript(createJobInput.getScript(),errors);
            validateType(createJobInput.getType(),errors);
        } else if (BuildJobInput.class.isInstance(target)) {
            BuildJobInput buildJobInput = (BuildJobInput) target;
            validateProjectId(buildJobInput.getProjectId(),errors);
            validateBranch(buildJobInput.getBranch(),errors);
        } else if (BuildLogsInput.class.isInstance(target)) {
            BuildLogsInput buildLogsInput = (BuildLogsInput) target;
            teamValidaor.validateId(buildLogsInput.getTeamId(),"teamId",errors);
            validatePage(buildLogsInput.getPage(),errors);

        } else if (UpdateJobInput.class.isInstance(target)) {
            UpdateJobInput updateJobInput = (UpdateJobInput) target;
            validateJobId(updateJobInput.getId(),errors);
            teamValidaor.validateId(updateJobInput.getTeamId(),"teamId",errors);
            validateProjectId(updateJobInput.getProjectId(),errors);
            validateType(updateJobInput.getType(),errors);
            validateScript(updateJobInput.getScript(),errors);
        }
    }

    public void validateProjectId (Integer projectId,Errors errors) {

        if (projectId == null) {
            errors.rejectValue("projectId",String.valueOf(NestStatusCode.PROJECT_ID_IS_EMPTY),"项目id不能为空");
            return;
        }

        Project project = projectDao.getProjectById(projectId);

        if (project == null) {
            errors.rejectValue("projectId",String.valueOf(NestStatusCode.PROJECT_NOT_EXIST),"项目不存在");
        }
    }

    public void validateScript (String shell,Errors errors) {

        if (StringUtils.isEmpty(shell)) {
            errors.rejectValue("shell",String.valueOf(NestStatusCode.SHELL_IS_EMPTY));
        }
    }

    public void validateType (Integer type,Errors errors) {

        if (type == null) {
            errors.rejectValue("type",String.valueOf(NestStatusCode.TYPE_IS_EMPTY),"job类型不能为空");
        }
    }

    public void validateBranch (String branch,Errors errors) {

        if (StringUtils.isEmpty(branch)) {
            errors.rejectValue("branch",String.valueOf(NestStatusCode.BRANCH_IS_EMPTY),"分支名不能为空");
        }
    }

    public void validatePage(Integer page,Errors errors) {

        if (page == null) {
            errors.rejectValue("page",String.valueOf(NestStatusCode.PAGE_IS_EMPTY));
        }

        if (page < 0) {
            errors.rejectValue("page",String.valueOf(NestStatusCode.PAGE_IS_ERROR));
        }
    }

    public void validateSize(Integer size,Errors errors) {

        if (size == null) {
            errors.rejectValue("size",String.valueOf(NestStatusCode.SIZE_IS_EMPTY));
        }

        if (size < 0) {
            errors.rejectValue("size",String.valueOf(NestStatusCode.SIZE_IS_ERROR));
        }
    }

    public void validateJobId(Integer id,Errors errors) {

        if (id == null) {
            errors.rejectValue("id",String.valueOf(NestStatusCode.JOB_ID_IS_EMPTY),"任务id为空");
            return;
        }

        Job job = jobDao.getJobById(id);

        if (job == null) {
            errors.rejectValue("id",String.valueOf(NestStatusCode.JOB_NOT_EXIST),"任务不存在");
        }
    }
}
