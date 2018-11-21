package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.BuildLogOutput;
import com.kiss.kissnest.output.GetBuildLogOutput;
import com.kiss.kissnest.output.JobOutput;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.CodeUtil;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import entity.Guest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.BeanCopyUtil;
import utils.ThreadLocalUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
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

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private CodeUtil codeUtil;

    @Value("${jenkins.buildLogUrl}")
    private String jenkinsBuildLogUrl;

    @Value("${jenkins.buildOutputPath}")
    private String jenkinsBuildOutputPath;

    @Value("${build.log.maxSize}")
    private String buildLogSize;

    @Value("${code.bin.ip}")
    private String codeIps;

    @Value("${gitlab.server.url}")
    private String gitlabUrl;

    @Value("${gitlab.server.commitPath}")
    private String gitlabCommitPath;

    @Autowired
    private DeployLogDao deployLogDao;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private EnvironmentDao environmentDao;

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private PackageRepositoryService jobRepositoryService;

    public static Map<String, String> buildRemarks = new HashMap<>();

    public static Map<String, String> deployRemarks = new HashMap<>();

    public ResultOutput createBuildJob(CreateJobInput createJobInput) {

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

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        boolean success = jenkinsUtil.createJobByShell(project.getSlug(), createJobInput.getScript(), projectRepository.getSshUrl(), guest.getUsername(), member.getApiToken());

        if (!success) {
            throw new TransactionalException(NestStatusCode.CREATE_JENKINS_JOB_ERROR);
        }

        Job job = new Job();
        job.setTeamId(project.getTeamId());
        job.setJobName(project.getSlug());
        job.setProjectId(projectId);
        job.setScript(createJobInput.getScript());
        job.setType(createJobInput.getType());
        job.setStatus(0);
        job.setNumber(0);

        jobDao.createJob(job);

        JobOutput jobOutput = BeanCopyUtil.copy(job,JobOutput.class);
        operationLogService.saveOperationLog(project.getTeamId(),guest,null,job,"id",OperationTargetType.TYPE__CREATE_JOB);
        operationLogService.saveDynamic(guest,job.getTeamId(),null,job.getProjectId(),OperationTargetType.TYPE__CREATE_JOB,job);
        return ResultOutputUtil.success(jobOutput);
    }

    public ResultOutput createDeployJob(CreateDeployInput createDeployInput) {

        Integer projectId = createDeployInput.getProjectId();
        Project project = projectDao.getProjectById(projectId);

        if (StringUtils.isEmpty(project.getSlug())) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_SLUG_EMPTY);
        }

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (StringUtils.isEmpty(member.getApiToken())) {
            return ResultOutputUtil.error(NestStatusCode.MEMBER_APITOKEN_IS_EMPTY);
        }

        List<Integer> serverIdList = createDeployInput.getServerIds();
        Job job = new Job();
        job.setTeamId(project.getTeamId());
        job.setJobName(project.getSlug());
        job.setProjectId(projectId);
        job.setConf(createDeployInput.getConf());
        job.setType(createDeployInput.getType());
        job.setEnvId(createDeployInput.getEnvId());
        job.setServerIds(serverIdList == null ? null : JSON.toJSONString(serverIdList));
        job.setType(2);

        jobDao.createJob(job);

        Integer id = job.getId();
        job = jobDao.getJobById(id);

        return ResultOutputUtil.success(BeanCopyUtil.copy(job,JobOutput.class,BeanCopyUtil.defaultFieldNames));
    }

    @Transactional
    public ResultOutput buildJob(BuildJobInput buildJobInput) {

        Job job = jobDao.getJobByProjectIdAndType(buildJobInput.getProjectId(), 1);
        Project project = projectDao.getProjectById(job.getProjectId());
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        BuildLog buildLog = saveBuildLog(job.getTeamId(),jobName,buildJobInput.getBranch(),buildJobInput.getProjectId(),guest);

        if (buildLog == null) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_BUILD_LOG_FAILED);
        }

        String location = jenkinsUtil.buildJob(jobName, buildJobInput.getBranch(), guest.getUsername(), member.getApiToken());

        if (location == null) {
            throw new TransactionalException(NestStatusCode.BUILD_JENKINS_JOB_ERROR);
        }

        location = location.endsWith("/") ? location.substring(0, location.length() - 1) : location;
        buildRemarks.put(location, buildJobInput.getRemark());
        String[] urlStr = location.split("/");
        Thread thread = new Thread(new BuildLogRunnable(buildLog.getId(),jobName, guest.getUsername(), member.getApiToken(), 1, location,buildJobInput.getType(),project.getId()));
        thread.start();
        operationLogService.saveOperationLog(job.getTeamId(),guest,job,null,"id",OperationTargetType.TYPE__BUILD_JOB);
        operationLogService.saveDynamic(guest,job.getTeamId(),null,job.getProjectId(),OperationTargetType.TYPE__BUILD_JOB,job);
        Map<String, Object> result = new HashMap<>();
        result.put("id",buildLog.getId());
        result.put("projectName", project.getName());
        result.put("projectId",buildJobInput.getProjectId());
        result.put("branch", buildJobInput.getBranch());
        result.put("remark", buildJobInput.getRemark());
        result.put("status",2);
        result.put("statusText",codeUtil.getEnumsMessage("build.status",String.valueOf(result.get("status"))));
        result.put("createdAt",buildLog.getCreatedAt() == null ? null : buildLog.getCreatedAt().getTime());
        return ResultOutputUtil.success(result);
    }

    public ResultOutput deployJob(DeployJobInput deployJobInput) {
        Job job = jobDao.getJobByProjectIdAndType(deployJobInput.getProjectId(), 2);
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        String branch = deployJobInput.getBranch() == null ? deployJobInput.getTag() : deployJobInput.getBranch();
        String location = jenkinsUtil.buildJob(jobName, branch, guest.getUsername(), member.getApiToken());

        if (location == null) {
            return ResultOutputUtil.error(NestStatusCode.DEPLOY_JENKINS_JOB_ERROR);
        }

        Integer count = jobDao.updateJobStatus(deployJobInput.getProjectId(), 2, 0, 1);

        if (count == 1) {
            Integer number = job.getNumber() + 1;
            Thread thread = new Thread(new DeployLogRunner(jobName, job.getTeamId(), deployJobInput.getProjectId(), guest.getId(), guest.getUsername(), member.getApiToken(), number, 2, job.getServerIds()));
            thread.start();
            deployRemarks.put(deployJobInput.getProjectId() + "" + number, deployJobInput.getRemark());
        }

        return ResultOutputUtil.success();
    }

    public ResultOutput validateJobExist(Integer projectId, Integer type) {

        Map<String, Boolean> result = new HashMap<>();
        Job job = jobDao.getJobByProjectIdAndType(projectId, type);

        if (job == null) {
            result.put("exist", false);
        } else {
            result.put("exist", true);
        }

        return ResultOutputUtil.success(result);
    }

    public ResultOutput getBuildLogsByTeamId(BuildLogsInput buildLogsInput) {

        Integer maxSize = Integer.parseInt(buildLogSize);
        Integer size = buildLogsInput.getSize();
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = buildLogsInput.getPage() == 0 ? null : (buildLogsInput.getPage() - 1) * pageSize;
        List<BuildLogOutput> buildLogOutputList = buildLogDao.getBuildLogsByTeamId(buildLogsInput.getTeamId(), start, pageSize);

        buildLogOutputList.forEach(buildLogOutput -> {
            buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));
            String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
            buildLogOutput.setCommitPath(commitPath);
        });

        Integer count = buildLogDao.getBuildLogCountByTeamId(buildLogsInput.getTeamId());
        GetBuildLogOutput buildLogOutputs = new GetBuildLogOutput();
        buildLogOutputs.setBuildLogOutputs(buildLogOutputList);
        buildLogOutputs.setCount(count);

        return ResultOutputUtil.success(buildLogOutputs);
    }

    public ResultOutput getBuildRecentLog(Integer id) {

        BuildLogOutput buildLogOutput = buildLogDao.getBuildRecentLog(id);

        if (buildLogOutput == null || buildLogOutput.getStatus() == null) {
            buildLogOutput = new BuildLogOutput();
            buildLogOutput.setId(id);
            buildLogOutput.setStatus(2);
            buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status",String.valueOf(buildLogOutput.getStatus())));
            return ResultOutputUtil.success(buildLogOutput);
        }

        String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
        buildLogOutput.setCommitPath(commitPath);
        buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status",String.valueOf(buildLogOutput.getStatus())));

        return ResultOutputUtil.success(buildLogOutput);
    }

    public ResultOutput getJobsByTeamId(Integer teamId, Integer type) {

        List<JobOutput> jobOutputs = jobDao.getJobOutputsByTeamId(teamId, type);

        return ResultOutputUtil.success(jobOutputs);
    }

    public ResultOutput updateBuildJob(UpdateJobInput updateJobInput) {

        Job job = BeanCopyUtil.copy(updateJobInput,Job.class);
        Integer count = jobDao.updateBuildJob(job);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_JOB_FAILED);
        }

        Job wholeJob = jobDao.getJobById(job.getId());
        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(job.getProjectId());

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (StringUtils.isEmpty(member.getApiToken())) {
            return ResultOutputUtil.error(NestStatusCode.MEMBER_APITOKEN_IS_EMPTY);
        }

        boolean success = jenkinsUtil.updateJob(wholeJob.getJobName(),updateJobInput.getScript(),projectRepository.getSshUrl(),guest.getUsername(),member.getApiToken());

        if (!success) {
            throw new TransactionalException(NestStatusCode.UPDATE_JENKINS_JOB_ERROR);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(job,JobOutput.class));
    }

    public ResultOutput updateDeployJob(UpdateDeployInput updateDeployInput) {

        Job job = BeanCopyUtil.copy(updateDeployInput,Job.class);
        Integer count = jobDao.updateDeployJob(job);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_DEPLOY_JOB_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(job,JobOutput.class));
    }

    public ResultOutput getProjectDeployConf(Integer projectId,Integer envId) {

        Project project = projectDao.getProjectById(projectId);
        Team team = teamDao.getTeamById(project.getTeamId());
        Group group = groupDao.getGroupById(project.getGroupId());
        String path = team.getSlug() + "-" + group.getSlug() + "-" + project.getSlug();
        Environment environment = environmentDao.getEnvironmentById(envId);
        String type = codeUtil.getEnumsMessage("environment.type.conf",String.valueOf(environment.getType()));

        if (project == null) {
            return ResultOutputUtil.error(NestStatusCode.PROJECT_NOT_EXIST);
        }

        StringBuilder stringBuilder = null;

        try {
            stringBuilder = jenkinsUtil.readFileFromClassPath("/supervisor.conf");

        } catch (IOException e) {
            e.printStackTrace();
            return ResultOutputUtil.error(NestStatusCode.GET_DEPLOY_CONF_FAILED);
        }

        String conf = String.format(stringBuilder.toString(),project.getName(),path,path,path,type,type,type,type,path,path);
        conf = StringEscapeUtils.unescapeXml(conf);
        Map<String,Object> result = new HashMap<>();
        result.put("conf",conf);

        return ResultOutputUtil.success(result);
    }

    class BuildLogRunnable implements Runnable {

        private Integer id;

        private String account;

        private String passwordOrToken;

        private Integer type;

        private String location;

        private String jobName;

        private Integer versionType;

        private Integer projectId;

        public BuildLogRunnable(Integer buildLogId,String jobName, String account, String passwordOrToken, Integer type, String location,Integer versionType,Integer projectId) {
            this.account = account;
            this.passwordOrToken = passwordOrToken;
            this.type = type;
            this.location = location;
            this.jobName = jobName;
            this.id = buildLogId;
            this.versionType = versionType;
            this.projectId = projectId;
        }

        @Override
        public void run() {
            Build build = null;
            JenkinsServer jenkinsServer = null;
            Integer newNumber = -1;
            try {
                jenkinsServer = jenkinsUtil.getJenkinsServer(account, passwordOrToken);

                if (jenkinsServer == null) {
                    return;
                }

                for (int i = 0; i < 300; i++) {
                    build = jenkinsUtil.getBuild(jenkinsServer, location);

                    if (build != null) {
                        break;
                    }

                    Thread.sleep(1000);
                }

                if (build == null) {
                    return;
                }

                updateBuildLog(build,id, jobName, account, location,versionType,projectId);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jenkinsServer != null) {
                    jenkinsUtil.close(jenkinsServer);
                }

//                if (newNumber != -1) {
//                    Integer count = jobDao.updateJobStatusAndNumber(projectId, type, 1, 0, newNumber);
//
//                    if (count == 0) {
//                        log.info("{}的{}的buildLog更新失败,操作人员{},记录条目数,新条目数{}", projectId, type, operatorId, newNumber);
//                    }
//                }
            }

        }
    }

    public BuildLog saveBuildLog(Integer teamId,String jobName,String branch,Integer projectId,Guest guest) {

        BuildLog buildLog = new BuildLog();
        buildLog.setTeamId(teamId);
        buildLog.setJobName(jobName);
        buildLog.setBranch(branch);
        buildLog.setProjectId(projectId);
        buildLog.setOperatorId(guest.getId());
        buildLog.setOperatorName(guest.getUsername());
        Integer count = buildLogDao.createBuildLog(buildLog);

        if (count == 0) {
            return null;
        }

        Integer id = buildLog.getId();
        buildLog = buildLogDao.getBuildLogById(id);

        return buildLog;
    }

    public void updateBuildLog(Build build,Integer id,String jobName,String account, String location,Integer versionType,Integer projectId) throws InterruptedException {

        JenkinsHttpConnection client = build.getClient();
        String logUrl = String.format(jenkinsBuildLogUrl, jobName, build.getNumber());
        BuildWithDetails buildWithDetails = jenkinsUtil.getLastBuildWithDetail(client, logUrl);

        for (int i = 0; i < 1000; i++) {
            buildWithDetails = jenkinsUtil.getLastBuildWithDetail(client, logUrl);
            boolean run = buildWithDetails.isBuilding();
            if (run) {
                Thread.sleep(3000);
            } else {
                break;
            }
        }

        client = buildWithDetails.getClient();
        String output = jenkinsUtil.getConsoleOutputText(client, logUrl + jenkinsBuildOutputPath);
        String result = buildWithDetails.getResult().name();
        Long duration = buildWithDetails.getDuration();
        Map<String, String> params = buildWithDetails.getParameters();
        String branch = params.get("branch");
        BuildLog buildLog = new BuildLog();
        buildLog.setId(id);
        buildLog.setBranch(branch);
        buildLog.setType(versionType);
        buildLog.setNumber(build.getNumber());
        buildLog.setOperatorName(account);
        buildLog.setOutput(output);
        buildLog.setBuildAt(buildWithDetails.getTimestamp());
        buildLog.setRemark(buildRemarks.get(location));
        buildRemarks.remove(location);
        String[] urlStr = location.split("/");
        buildLog.setQueueId(Long.valueOf(urlStr[urlStr.length - 1]));
        buildLog.setDuration(duration);

        if (output.indexOf("versionStart") != -1) {
            String version = output.substring(output.indexOf("versionStart") + 13, output.indexOf("versionEnd") - 1);
            buildLog.setVersion(version);
        }

        if (output.indexOf("jarNameStart") != -1) {
            String jarName = output.substring(output.indexOf("jarNameStart") + 13, output.indexOf("jarNameEnd") - 1);
            buildLog.setJarName(jarName);
        }

        if ("success".equalsIgnoreCase(result)) {
            buildLog.setStatus(1);
        } else {
            buildLog.setStatus(0);
        }

        buildLogDao.updateBuildLog(buildLog);
        buildLog.setProjectId(projectId);
        jobRepositoryService.createPackageRepository(buildLog);
    }

    class DeployLogRunner implements Runnable {

        private String jobName;

        private Integer teamId;

        private Integer projectId;

        private String account;

        private String passwordOrToken;

        private Integer operatorId;

        private Integer number;

        private Integer type;

        private String serverIds;

        public DeployLogRunner(String jobName, Integer teamId, Integer projectId, Integer operatorId, String account, String passwordOrToken, Integer number, Integer type, String serverIds) {
            this.jobName = jobName;
            this.projectId = projectId;
            this.account = account;
            this.passwordOrToken = passwordOrToken;
            this.operatorId = operatorId;
            this.number = number;
            this.type = type;
            this.teamId = teamId;
            this.serverIds = serverIds;
        }

        @Override
        public void run() {
            Build build = null;
            JenkinsServer jenkinsServer = jenkinsUtil.getJenkinsServer(account, passwordOrToken);

            if (jenkinsServer == null) {
                return;
            }
            //获取最后一个任务num
            Build lastBuild = jenkinsUtil.getLastBuild(jobName, jenkinsServer);
            if (lastBuild == null) {
                return;
            }

            Integer last = lastBuild.getNumber();
            Integer newNumber = number;

            if (last < number) {
                //num < number 等待
                for (int i = 0; i < 10; i++) {

                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    build = jenkinsUtil.getBuild(jobName, jenkinsServer, number);

                    if (build != null) {
                        break;
                    }
                }

                if (build != null) {
                    saveDeployLog(build, teamId, jobName, operatorId, account, projectId, number, serverIds);
                }
            } else if (last == number) {
                //num = number 将num插入数据库
                saveDeployLog(lastBuild, teamId, jobName, operatorId, account, projectId, number, serverIds);
            } else {
                //num > number 查询number-num的所有任务

                for (int i = number; i <= last; i++) {
                    build = jenkinsUtil.getBuild(jobName, jenkinsServer, i);

                    if (build != null) {
                        saveDeployLog(build, teamId, jobName, operatorId, account, projectId, i, serverIds);
                    }
                }

                newNumber = last;
            }

            Integer count = jobDao.updateJobStatusAndNumber(projectId, type, 2, 0, newNumber);

            if (count == 0) {
                log.info("{}的{}的deployLog更新失败,操作人员{},记录条目数,新条目数{}", projectId, type, operatorId, number, newNumber);
            }

            jenkinsUtil.close(jenkinsServer);
        }
    }

    public void saveDeployLog(Build build, Integer teamId, String jobName, Integer operatorId, String account, Integer projectId, Integer number, String serverIds) {

        JenkinsHttpConnection client = build.getClient();
        String logUrl = String.format(jenkinsBuildLogUrl, jobName, number);
        BuildWithDetails buildWithDetails = jenkinsUtil.getLastBuildWithDetail(client, logUrl);
        client = buildWithDetails.getClient();
        String output = jenkinsUtil.getConsoleOutputText(client, logUrl + jenkinsBuildOutputPath);
        String result = buildWithDetails.getResult().name();
        Map<String, String> params = buildWithDetails.getParameters();
        String branch = params.get("branch");
        DeployLog deployLog = new DeployLog();
        deployLog.setTeamId(teamId);
        deployLog.setJobName(jobName);
        deployLog.setServerIds(serverIds);
        deployLog.setBranch(branch);
        deployLog.setNumber(build.getNumber());
        deployLog.setProjectId(projectId);
        deployLog.setRemark(deployRemarks.get(projectId + "" + number));
        deployLog.setOperatorId(operatorId);
        deployLog.setOperatorName(account);
        deployLog.setOutput(output);
        deployLog.setDeployAt(buildWithDetails.getTimestamp());

        if (output.indexOf("versionStart") != -1) {
            String version = output.substring(output.indexOf("versionStart") + 13, output.indexOf("versionEnd") - 1);
            deployLog.setVersion(version);
        }

        if ("success".equalsIgnoreCase(result)) {
            deployLog.setStatus(0);
        } else {
            deployLog.setStatus(1);
        }

        deployLogDao.createDeployLog(deployLog);
        deployRemarks.remove(projectId + "" + number);
    }
}
