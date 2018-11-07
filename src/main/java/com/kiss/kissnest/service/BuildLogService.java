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
import com.offbytwo.jenkins.JenkinsServer;
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
public class BuildLogService {

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

        boolean run = true;
        Integer number = 1;

        synchronized (projectDao) {
            BuildLog buildLog = buildLogDao.getLastBuildByJobNameAndProjectId(jobName,buildJobInput.getProjectId());

            if (buildLog != null) {
                if (buildLog.getNumber() != null) {
                    number = buildLog.getNumber() + 1;
                } else {
                    run = false;
                }
            }
        }

        if (run) {
            Thread thread = new Thread(new BuildLogRunnable(jobName,buildJobInput.getProjectId(),guest.getId(),guest.getName(),member.getApiToken(),number));
            thread.start();
        }

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

        private Integer number;

        public BuildLogRunnable(String jobName,Integer projectId,Integer operatorId,String account,String passwordOrToken,Integer number) {
            this.jobName = jobName;
            this.projectId = projectId;
            this.account = account;
            this.passwordOrToken = passwordOrToken;
            this.operatorId = operatorId;
            this.number = number;
        }

        @Override
        public void run() {
            Build build = null;
            JenkinsServer jenkinsServer = jenkinsUtil.getJenkinsServer(account,passwordOrToken);

            for (int i=0;i<10;i++) {
                if (jenkinsServer == null) {
                    return;
                }

                build = jenkinsUtil.getBuild(jobName,jenkinsServer,number);

                if (build == null) {
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            saveBuildLog(build,jobName,operatorId,account,projectId,number);

            jenkinsUtil.close(jenkinsServer);
        }
    }

    public void saveBuildLog(Build build,String jobName,Integer operatorId,String account,Integer projectId,Integer number) {

        JenkinsHttpConnection client =build.getClient();
        String logUrl = String.format(jenkinsBuildLogUrl,jobName,number);
        BuildWithDetails buildWithDetails = jenkinsUtil.getLastBuildWithDetail(client,logUrl);
        client = buildWithDetails.getClient();
        String output = jenkinsUtil.getConsoleOutputText(client,logUrl + jenkinsBuildOutputPath);
        String result = buildWithDetails.getResult().name();
        Map<String,String> params = buildWithDetails.getParameters();
        String branch = params.get("branch");
        BuildLog buildLog = new BuildLog();
        buildLog.setBranch(branch);
        buildLog.setJobName(jobName);
        buildLog.setNumber(build.getNumber());
        buildLog.setOperatorId(operatorId);
        buildLog.setOperatorName(account);
        buildLog.setOutput(output);
        buildLog.setProjectId(projectId);

        if ("success".equalsIgnoreCase(result)) {
            buildLog.setStatus(0);
        } else {
            buildLog.setStatus(1);
        }

        buildLogDao.createBuildLog(buildLog);
    }
}
