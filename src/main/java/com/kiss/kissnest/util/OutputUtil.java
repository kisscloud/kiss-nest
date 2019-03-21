package com.kiss.kissnest.util;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.output.BuildLogOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OutputUtil {

    @Autowired
    private LangUtil langUtil;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ProjectDao projectDao;

    @Value("${gitlab.server.url}")
    private String gitlabUrl;

    @Value("${gitlab.server.commitPath}")
    private String gitlabCommitPath;

    @Value("${gitlab.server.branchPath}")
    private String gitlabBranchPath;

    public BuildLogOutput toBuildLogOutput(BuildLog buildLog) {

        Group group = groupDao.getGroupByProjectId(buildLog.getProjectId());
        Project project = projectDao.getProjectById(buildLog.getProjectId());

        BuildLogOutput buildLogOutput = new BuildLogOutput();
        BeanUtils.copyProperties(buildLog, buildLogOutput);

        if (buildLog.getStatus() != null) {
            buildLogOutput.setStatusText(langUtil.getEnumsMessage("build.status", String.valueOf(buildLog.getStatus())));
        }

        String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
        String branchPath = gitlabUrl + String.format(gitlabBranchPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getBranch());
        buildLogOutput.setCommitPath(commitPath);
        buildLogOutput.setBranchPath(branchPath);
        buildLogOutput.setProjectName(project.getName());
        buildLogOutput.setGroupName(group.getName());

        return buildLogOutput;
    }
}
