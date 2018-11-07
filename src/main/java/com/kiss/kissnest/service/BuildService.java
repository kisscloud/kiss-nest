package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.BuildLogDao;
import com.kiss.kissnest.dao.JobDao;
import com.kiss.kissnest.dao.MemberDao;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.entity.BuildLog;
import com.kiss.kissnest.entity.Job;
import com.kiss.kissnest.entity.Member;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.input.BuildJobInput;
import com.kiss.kissnest.input.CreateJobInput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import entity.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.ThreadLocalUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class BuildService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private JobDao jobDao;

    @Autowired
    private BuildLogDao buildLogDao;

    @Value("${jenkins.buildLogUrl}")
    private String jenkinsBuildLogUrl;

    @Value("${jenkins.buildOutputPath}")
    private String jenkinsBuildOutputPath;

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

        boolean success = jenkinsUtil.createJobByShell(project.getSlug(),createJobInput.getScript(),guest.getName(),member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_JENKINS_JOB_ERROR);
        }

        Job job = new Job();
        job.setJobName(project.getSlug());
        job.setProjectId(projectId);
        job.setShell(createJobInput.getScript());
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

        Thread thread = new Thread(new BuildLogRunnable(jobName,buildJobInput.getProjectId(),guest.getId(),guest.getName(),member.getApiToken()));
        thread.start();
        return ResultOutputUtil.success();
    }

    public ResultOutput validateJobExist(Integer projectId,Integer type) {

        Map<String,Boolean> result = new HashMap<>();
        Job job = jobDao.getJobByProjectIdAndType(projectId,type);

        if (job == null) {
            result.put("exist",false);
        } else {
            result.put("exist",true);
        }

        return ResultOutputUtil.success(result);
    }

    class BuildLogRunnable implements Runnable {

        private String jobName;

        private Integer projectId;

        private String account;

        private String passwordOrToken;

        private Integer operatorId;

        public BuildLogRunnable(String jobName,Integer projectId,Integer operatorId,String account,String passwordOrToken) {
            this.jobName = jobName;
            this.projectId = projectId;
            this.account = account;
            this.passwordOrToken = passwordOrToken;
            this.operatorId = operatorId;
        }

        @Override
        public void run() {

            //根据项目名查询构建的num
            BuildLog buildLog = buildLogDao.getLastBuildByJobNameAndProjectId(jobName,projectId);
            Integer num = buildLog.getNumber();
            //获取最后一次构建的build
            Build lastBuild = jenkinsUtil.getLastBuild(jobName,account,passwordOrToken);
            Integer last = lastBuild.getNumber();
            //num跟最后一次的num比较
            //num > last,直接不做处理
            if (num > last) {
                return;
            }
            //num == last - 1,就查询last的log就行
            if (num == last - 1) {
                JenkinsHttpConnection client =lastBuild.getClient();
                BuildWithDetails buildWithDetails = jenkinsUtil.getLastBuildWithDetail(client,jenkinsBuildLogUrl);
                String output = jenkinsUtil.getConsoleOutputText(client,jenkinsBuildLogUrl + jenkinsBuildOutputPath);
                String result = buildWithDetails.getResult().name();
                Map<String,String> params = buildWithDetails.getParameters();
                String branch = params.get("branch");

            }
            //num < last - 1,冲num+1查询log开始到last - 1

            //num = last,等待60s,每5s有一个循环，60s,之后终结
            System.out.println(jobName);
        }
    }
}
