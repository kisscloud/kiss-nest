package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.output.ProjectOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.BeanCopyUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import output.ResultOutput;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectDao projectDao;

    public ResultOutput createProject(Project project) {

        List<Project> projectList = projectDao.getProjectsByName(project.getName());

        if (projectList != null && projectList.size() != 0) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NAME_EXIST);
        }

        Integer count = projectDao.createProject(project);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_PROJECT_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(project,ProjectOutput.class));
    }

    public ResultOutput deleteProject(Integer id) {

        Project project = projectDao.getProjectById(id);

        if (project == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        Integer count = projectDao.deleteProjectById(id);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.DELETE_PROJECT_FAILED);
        }

        return ResultOutputUtil.success();
    }

    public ResultOutput updateProject(Project project) {

        Project exist = projectDao.getProjectById(project.getId());

        if (exist == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        Integer count = projectDao.updateProject(project);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_PROJECT_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(project,ProjectOutput.class));
    }

    public ResultOutput getProjectById(Integer id) {

        Project project = projectDao.getProjectById(id);

        return ResultOutputUtil.success(BeanCopyUtil.copy(project,ProjectOutput.class));
    }

    public ResultOutput getProjects() {

        List<Project> projects = projectDao.getProjects();

        return ResultOutputUtil.success(BeanCopyUtil.copyList(projects,ProjectOutput.class));
    }
}
