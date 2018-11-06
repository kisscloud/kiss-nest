package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.BuildJobInput;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.ThreadLocalUtil;

@Service
public class BuildLogService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private JobDao jobDao;

    public ResultOutput createJob(CreateJobInput createJobInput) {

        Integer projectId = createJobInput.getProjectId();
        Project project = projectDao.getProjectById(projectId);

        if (StringUtils.isEmpty(project.getSlug())) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_SLUG_EMPTY);
        }

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (StringUtils.isEmpty(member.getApiToken())) {
            return ResultOutputUtil.error(NestStatusCode.MEMBER_APITOKEN_IS_EMPTY);
        }

        boolean success = jenkinsUtil.createJobByShell(project.getSlug(),createJobInput.getShell(),guest.getName(),member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_JENKINS_JOB_ERROR);
        }

        Job job = new Job();
        job.setJobName(project.getSlug());
        job.setProjectId(projectId);
        job.setShell(createJobInput.getShell());
        job.setType(createJobInput.getType());
        jobDao.createJob(job);
        return ResultOutputUtil.success();
    }

    public ResultOutput buildJob(BuildJobInput buildJobInput) {

        Job job = jobDao.getJobByProjectId(buildJobInput.getProjectId());
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        boolean success = jenkinsUtil.buildJob(jobName,buildJobInput.getBranch(),guest.getName(),member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.BUILD_JENKINS_JOB_ERROR);
        }

        return ResultOutputUtil.success();
    }
}
