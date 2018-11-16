package com.kiss.kissnest.service;

import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.BuildLogOutput;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import output.ResultOutput;
import utils.BeanCopyUtil;
import utils.ThreadLocalUtil;
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

    @Autowired
    private DeployLogDao deployLogDao;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private OperationLogService operationLogService;

    public static Map<String,String> buildRemarks = new HashMap<>();

    public static Map<String,String> deployRemarks = new HashMap<>();

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

        boolean success = jenkinsUtil.createJobByShell(project.getSlug(), createJobInput.getScript(),projectRepository.getSshUrl(), guest.getName(), member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_JENKINS_JOB_ERROR);
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

//        operationLogService.saveOperationLog(project.getTeamId(),guest,null,job,"id",OperationTargetType.TYPE__CREATE_JOB);
        return ResultOutputUtil.success();
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

        String serverIds = String.join(",",createDeployInput.getServerIds());
        List<String> serverIps = serverDao.getServerInnerIpsByIds(serverIds);

        String script = createDeployInput.getScript();

        for (String serverIp : serverIps) {
            String ansibleScript = "\n./ansible " +codeIps+ " -u root -m shell -a \"rsync -v /opt/app/" +project.getSlug()+ "$name.tar.gz root@" +serverIp+ ":/root/\"\n";
            script = script + ansibleScript;
        }

        boolean success = jenkinsUtil.createJobByShell(project.getSlug(), createDeployInput.getScript(),null, guest.getName(), member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.CREATE_JENKINS_JOB_ERROR);
        }

        Job job = new Job();
        job.setTeamId(project.getTeamId());
        job.setJobName(project.getSlug());
        job.setProjectId(projectId);
        job.setScript(createDeployInput.getScript());
        job.setType(createDeployInput.getType());
        job.setEnvId(createDeployInput.getEnvId());
        job.setServerIds(serverIds);
        job.setStatus(0);
        job.setNumber(0);

        jobDao.createJob(job);

        return ResultOutputUtil.success();
    }

    public ResultOutput buildJob(BuildJobInput buildJobInput) {

        Job job = jobDao.getJobByProjectIdAndType(buildJobInput.getProjectId(),1);
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        boolean success = jenkinsUtil.buildJob(jobName, buildJobInput.getBranch(), guest.getName(), member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.BUILD_JENKINS_JOB_ERROR);
        }

        Integer count = jobDao.updateJobStatus(buildJobInput.getProjectId(), 1, 0, 1);

        if (count == 1) {
            Integer number = job.getNumber() + 1;
            Thread thread = new Thread(new BuildLogRunnable(jobName, job.getTeamId(), buildJobInput.getProjectId(), guest.getId(), guest.getName(), member.getApiToken(), number, 1));
            thread.start();
            buildRemarks.put(buildJobInput.getProjectId() + "" + number,buildJobInput.getRemark());
        }

//        operationLogService.saveOperationLog(job.getTeamId(),guest,job,null,"id",OperationTargetType.TYPE__BUILD_JOB);

        return ResultOutputUtil.success();
    }

    public ResultOutput deployJob(DeployJobInput deployJobInput) {
        Job job = jobDao.getJobByProjectIdAndType(deployJobInput.getProjectId(),2);
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        String branch = deployJobInput.getBranch() == null ? deployJobInput.getTag() : deployJobInput.getBranch();
        boolean success = jenkinsUtil.buildJob(jobName, branch, guest.getName(), member.getApiToken());

        if (!success) {
            return ResultOutputUtil.error(NestStatusCode.DEPLOY_JENKINS_JOB_ERROR);
        }

        Integer count = jobDao.updateJobStatus(deployJobInput.getProjectId(), 2, 0, 1);

        if (count == 1) {
            Integer number = job.getNumber() + 1;
            Thread thread = new Thread(new DeployLogRunner(jobName, job.getTeamId(), deployJobInput.getProjectId(), guest.getId(), guest.getName(), member.getApiToken(), number, 2,job.getServerIds()));
            thread.start();
            deployRemarks.put(deployJobInput.getProjectId() + "" + number,deployJobInput.getRemark());
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
        List<BuildLog> buildLogs = buildLogDao.getBuildLogsByTeamId(buildLogsInput.getTeamId(),(buildLogsInput.getPage() - 1) * pageSize, pageSize);
        List<BuildLogOutput> buildLogOutputs = (List) BeanCopyUtil.copyList(buildLogs, BuildLogOutput.class);

        buildLogOutputs.forEach(buildLogOutput -> buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status",String.valueOf(buildLogOutput.getStatus()))));


        return ResultOutputUtil.success(buildLogOutputs);
    }

    public ResultOutput getBuildRecentLogs(Integer teamId,Integer projectId,Long timestamp) {

        List<BuildLog> buildLogs = buildLogDao.getBuildRecentLogs(teamId,projectId,timestamp);

        return ResultOutputUtil.success(buildLogs);
    }

    class BuildLogRunnable implements Runnable {

        private String jobName;

        private Integer teamId;

        private Integer projectId;

        private String account;

        private String passwordOrToken;

        private Integer operatorId;

        private Integer number;

        private Integer type;

        public BuildLogRunnable(String jobName, Integer teamId, Integer projectId, Integer operatorId, String account, String passwordOrToken, Integer number, Integer type) {
            this.jobName = jobName;
            this.projectId = projectId;
            this.account = account;
            this.passwordOrToken = passwordOrToken;
            this.operatorId = operatorId;
            this.number = number;
            this.type = type;
            this.teamId = teamId;
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

                Thread.sleep(5000);
                //获取最后一个任务num
                Build lastBuild = jenkinsUtil.getLastBuild(jobName, jenkinsServer);
                if (lastBuild == null) {
                    return;
                }

                Integer last = lastBuild.getNumber();
                newNumber = number;

                if (last < number) {
                    //num < number 等待
                    for (int i = 0; i < 10; i++) {

                        Thread.sleep(6000);
                        build = jenkinsUtil.getBuild(jobName, jenkinsServer, number);

                        if (build != null) {
                            break;
                        }
                    }

                    if (build != null) {
                        saveBuildLog(build, teamId, jobName, operatorId, account, projectId, number);
                    }
                } else if (last == number) {
                    //num = number 将num插入数据库
                    saveBuildLog(lastBuild, teamId, jobName, operatorId, account, projectId, number);
                } else {
                    //num > number 查询number-num的所有任务

                    for (int i = number; i <= last; i++) {
                        build = jenkinsUtil.getBuild(jobName, jenkinsServer, i);

                        if (build != null) {
                            saveBuildLog(build, teamId, jobName, operatorId, account, projectId, i);
                        }
                    }

                    newNumber = last;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jenkinsServer != null) {
                    jenkinsUtil.close(jenkinsServer);
                }

                if (newNumber != -1) {
                    Integer count = jobDao.updateJobStatusAndNumber(projectId, type, 1, 0, newNumber);

                    if (count == 0) {
                        log.info("{}的{}的buildLog更新失败,操作人员{},记录条目数,新条目数{}", projectId, type, operatorId, number, newNumber);
                    }
                }
            }

        }
    }

    public void saveBuildLog(Build build, Integer teamId, String jobName, Integer operatorId, String account, Integer projectId, Integer number) {

        JenkinsHttpConnection client = build.getClient();
        String logUrl = String.format(jenkinsBuildLogUrl, jobName, number);
        BuildWithDetails buildWithDetails = jenkinsUtil.getLastBuildWithDetail(client, logUrl);
        client = buildWithDetails.getClient();
        String output = jenkinsUtil.getConsoleOutputText(client, logUrl + jenkinsBuildOutputPath);
        String result = buildWithDetails.getResult().name();
        Map<String, String> params = buildWithDetails.getParameters();
        String branch = params.get("branch");
        BuildLog buildLog = new BuildLog();
        buildLog.setTeamId(teamId);
        buildLog.setBranch(branch);
        buildLog.setJobName(jobName);
        buildLog.setNumber(build.getNumber());
        buildLog.setOperatorId(operatorId);
        buildLog.setOperatorName(account);
        buildLog.setOutput(output);
        buildLog.setProjectId(projectId);
        buildLog.setBuildAt(buildWithDetails.getTimestamp());
        buildLog.setRemark(buildRemarks.get(projectId + "" + number));
        buildRemarks.remove(projectId + "" + number);

        if (output.indexOf("versionStart") != -1) {
            String version = output.substring(output.indexOf("versionStart") + 13,output.indexOf("versionEnd") -1);
            buildLog.setVersion(version);
        }

        if (output.indexOf("jarNameStart") != -1) {
            String jarName = output.substring(output.indexOf("jarNameStart") + 13,output.indexOf("jarNameEnd") -1);
            buildLog.setJarName(jarName);
        }

        if ("success".equalsIgnoreCase(result)) {
            buildLog.setStatus(0);
        } else {
            buildLog.setStatus(1);
        }

        buildLogDao.createBuildLog(buildLog);
    }

    class DeployLogRunner implements Runnable{

        private String jobName;

        private Integer teamId;

        private Integer projectId;

        private String account;

        private String passwordOrToken;

        private Integer operatorId;

        private Integer number;

        private Integer type;

        private String serverIds;

        public DeployLogRunner(String jobName, Integer teamId, Integer projectId, Integer operatorId, String account, String passwordOrToken, Integer number, Integer type,String serverIds) {
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
                    saveDeployLog(build, teamId, jobName, operatorId, account, projectId, number,serverIds);
                }
            } else if (last == number) {
                //num = number 将num插入数据库
                saveDeployLog(lastBuild, teamId, jobName, operatorId, account, projectId, number,serverIds);
            } else {
                //num > number 查询number-num的所有任务

                for (int i = number; i <= last; i++) {
                    build = jenkinsUtil.getBuild(jobName, jenkinsServer, i);

                    if (build != null) {
                        saveDeployLog(build, teamId, jobName, operatorId, account, projectId, i,serverIds);
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

    public void saveDeployLog(Build build, Integer teamId, String jobName, Integer operatorId, String account, Integer projectId, Integer number,String serverIds) {

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
            String version = output.substring(output.indexOf("versionStart") + 13,output.indexOf("versionEnd") -1);
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
