package com.kiss.kissnest.util;

import com.kiss.kissnest.dao.GroupDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.entity.Group;
import com.kiss.kissnest.output.BuildLogOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutputUtil {

    @Autowired
    private LangUtil langUtil;

    @Autowired
    private GroupDao groupDao;

    public BuildLogOutput toBuildLogOutput(BuildLog buildLog) {
        Group group = groupDao.getGroupByProjectId(buildLog.getProjectId());
        BuildLogOutput buildLogOutput = new BuildLogOutput();
        BeanUtils.copyProperties(buildLogOutput, buildLog);
        buildLogOutput.setStatusText(langUtil.getEnumsMessage("build.status", String.valueOf(buildLog.getStatus())));
        buildLogOutput.setGroupName(group.getName());
        return buildLogOutput;
    }
}
