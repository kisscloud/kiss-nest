package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.*;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.CodeUtil;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import com.kiss.kissnest.util.SaltStackUtil;
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
import utils.GuestUtil;
import utils.ThreadLocalUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class JobService {

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

    @Value("${gitlab.server.url}")
    private String gitlabUrl;

    @Value("${gitlab.server.commitPath}")
    private String gitlabCommitPath;

    @Value("${gitlab.server.branchPath}")
    private String gitlabBranchPath;

    @Value("${package.nginx.url}")
    private String packageUrl;

    @Value("${package.config.url}")
    private String configUrl;

    @Value(("${saltstack.url}"))
    private String saltStackUrl;

    @Value(("${saltstack.username}"))
    private String saltStatckUsername;

    @Value(("${saltstack.password}"))
    private String saltStatckPassword;

    @Autowired
    private DeployLogDao deployLogDao;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private EnvironmentDao environmentDao;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private PackageRepositoryService packageRepositoryService;

    @Autowired
    private PackageRepositoryDao packageRepositoryDao;

    @Autowired
    private SaltStackUtil saltStackUtil;

    @Autowired
    private GroupDao groupDao;


    public static Map<String, String> buildRemarks = new HashMap<>();

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

        boolean success = jenkinsUtil.createJobByShell(project.getSlug(), projectRepository.getPathWithNamespace(), createJobInput.getScript(), projectRepository.getSshUrl(), guest.getUsername(), member.getApiToken());

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
        job = jobDao.getJobById(job.getId());

        JobOutput jobOutput = BeanCopyUtil.copy(job, JobOutput.class);
        operationLogService.saveOperationLog(project.getTeamId(), guest, null, job, "id", OperationTargetType.TYPE__CREATE_BUILD_JOB);
        operationLogService.saveDynamic(guest, job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__CREATE_BUILD_JOB, job);
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
        job.setScript(createDeployInput.getScript());
        job.setServerIds(serverIdList == null ? null : JSON.toJSONString(serverIdList));
        job.setType(2);
        jobDao.createJob(job);
        Integer id = job.getId();
        JobOutput jobOutput = jobDao.getJobOutputsById(id);

        operationLogService.saveOperationLog(project.getTeamId(), guest, null, job, "id", OperationTargetType.TYPE__CREATE_DEPLOY_JOB);
        operationLogService.saveDynamic(guest, job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__CREATE_DEPLOY_JOB, job);

        return ResultOutputUtil.success(jobOutput);
    }

    @Transactional
    public ResultOutput buildJob(BuildJobInput buildJobInput) {

        Job job = jobDao.getJobByProjectIdAndType(buildJobInput.getProjectId(), 1);
        Project project = projectDao.getProjectById(job.getProjectId());
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        BuildLog buildLog = saveBuildLog(job.getTeamId(), jobName, buildJobInput.getBranch(), buildJobInput.getProjectId(), guest, 2);

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
        Thread thread = new Thread(new BuildLogRunnable(buildLog.getId(), jobName, guest.getUsername(), guest.getName(), member.getApiToken(), 1, location, buildJobInput.getType(), project.getId()));
        thread.start();
        operationLogService.saveOperationLog(job.getTeamId(), guest, job, null, "id", OperationTargetType.TYPE__BUILD_JOB);
        operationLogService.saveDynamic(guest, job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__BUILD_JOB, job);
        Group group = groupDao.getGroupByProjectId(buildJobInput.getProjectId());
        Map<String, Object> result = new HashMap<>();
        result.put("id", buildLog.getId());
        result.put("projectName", project.getName());
        result.put("projectId", buildJobInput.getProjectId());
        result.put("branch", buildJobInput.getBranch());
        result.put("remark", buildJobInput.getRemark());
        result.put("status", 2);
        result.put("statusText", codeUtil.getEnumsMessage("build.status", String.valueOf(result.get("status"))));
        result.put("createdAt", buildLog.getCreatedAt() == null ? null : buildLog.getCreatedAt().getTime());
        result.put("groupId",group.getId());
        result.put("groupName",group.getName());
        return ResultOutputUtil.success(result);
    }

    public ResultOutput deployJob(DeployJobInput deployJobInput) throws IOException {
        Job job = jobDao.getJobByProjectIdAndType(deployJobInput.getProjectId(), 2);
        Environment environment = environmentDao.getEnvironmentById(job.getEnvId());
        Integer type = environment.getType();
        String tarName;
        String jarName;
        String version;
        if (type == 1) {
            //测试环境
            PackageRepository packageRepository = new PackageRepository();
            packageRepository.setProjectId(deployJobInput.getProjectId());
            packageRepository.setBranch(deployJobInput.getBranch());
            packageRepository = packageRepositoryDao.getPackageRepository(packageRepository);
            tarName = packageRepository.getTarName();
            jarName = packageRepository.getJarName();
            version = packageRepository.getVersion();
        } else {
            PackageRepository packageRepository = new PackageRepository();
            packageRepository.setProjectId(deployJobInput.getProjectId());
            packageRepository.setTag(deployJobInput.getTag());
            packageRepository = packageRepositoryDao.getPackageRepository(packageRepository);
            tarName = packageRepository.getTarName();
            jarName = packageRepository.getJarName();
            version = packageRepository.getVersion();
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(deployJobInput.getProjectId());
        String path = projectRepository.getPathWithNamespace();

        String script = job.getScript();
//        String shell = String.format(script,path);
        script = script.replace("__TAR__PACKAGE__", packageUrl + path + "/" + tarName);
        String slug = path.replace("/", "-");
        path = path.substring(0, path.lastIndexOf("/") + 1);
        script = script.replace("__CONFIG__", configUrl + ":" + path + "config.git");

        String option = "mkdir -p /opt/option/" + path + " && cd /opt/option/" + path + " && echo '" + script + "' > " + deployJobInput.getProjectId() + ".sh && chmod +x " + deployJobInput.getProjectId() + ".sh"
                + " && ./" + deployJobInput.getProjectId() + ".sh";

        String conf = job.getConf();
        conf = conf.replace("__PACKAGE__", jarName);
        conf = conf.replace("__CONFIG__", "/opt/configs/" + path + "config");
        option = option + " && cd /etc/supervisor/conf.d && echo '" + conf + "' > " + slug + ".conf && supervisorctl reread && supervisorctl update && echo $?";

        String serverIds = job.getServerIds();
        serverIds = StringUtils.isEmpty(serverIds) ? "" : serverIds.substring(1, serverIds.length() - 1);
        String targetIps = serverDao.getServerIpsByIds(serverIds);

        log.info("操作{},目标{},conf{}", option, targetIps, conf);

        String response = saltStackUtil.callLocalSync(environment.getSaltHost(), environment.getSaltUser(), environment.getSaltPassword(), environment.getSaltVersion(), "cmd.run", targetIps, option);

        log.info("部署日志:{}", response);

        Integer success = 0;
        Integer total = 0;
        if (!StringUtils.isEmpty(response)) {
            JSONObject returnJson = JSONObject.parseObject(response);
            JSONArray returnArray = returnJson.getJSONArray("return");
            JSONObject node = returnArray.getJSONObject(0);
            String[] target = targetIps.split(",");

            for (int i = 0; i < target.length; i++) {
                String message = node.getString(target[i]);
                System.out.println(message);
                System.out.println(message.lastIndexOf("\n"));
                String status = message.substring(message.lastIndexOf("\n") + 1);
                log.info("status:{}", status);

                if (status.equals("0")) {
                    success++;
                }

                total++;
            }
        }

        DeployLog deployLog = new DeployLog();
        deployLog.setTeamId(job.getTeamId());
        deployLog.setJobId(job.getId());
        deployLog.setEnvId(environment.getId());
        deployLog.setBranch(deployJobInput.getBranch());
        deployLog.setTag(deployJobInput.getTag());
        deployLog.setVersion(version);
        deployLog.setProjectId(job.getProjectId());
        deployLog.setRemark(deployJobInput.getRemark());
        deployLog.setStatus(1);
        deployLog.setStatusText(success + "/" + total);
        deployLog.setOutput(response);
        deployLog.setOperatorId(GuestUtil.getGuestId());
        deployLog.setOperatorName(GuestUtil.getName());
        deployLogDao.createDeployLog(deployLog);
        DeployLogOutput deployLogOutput = deployLogDao.getDeployLogOutputById(deployLog.getId());
        String commitPath = gitlabUrl + String.format(gitlabCommitPath, deployLogOutput.getCommitPath() == null ? "" : deployLogOutput.getCommitPath(), deployLogOutput.getVersion());
        String branchPath = gitlabUrl + String.format(gitlabBranchPath, deployLogOutput.getCommitPath() == null ? "" : deployLogOutput.getCommitPath(), deployLogOutput.getBranch());
        deployLogOutput.setCommitPath(commitPath);
        deployLogOutput.setBranchPath(branchPath);
        deployLogOutput.setServerIds("[" + targetIps + "]");

        operationLogService.saveOperationLog(job.getTeamId(), ThreadLocalUtil.getGuest(), null, deployLog, "id", OperationTargetType.TYPE__DEPLOY_JOB);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(), job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__DEPLOY_JOB, deployLogOutput);

        return ResultOutputUtil.success(deployLogOutput);
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

    public ResultOutput getBuildLogsByTeamId(BuildLogInput buildLogsInput) {

        Integer maxSize = Integer.parseInt(buildLogSize);
        Integer size = buildLogsInput.getSize();
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = buildLogsInput.getPage() == 0 ? null : (buildLogsInput.getPage() - 1) * pageSize;
        List<BuildLogOutput> buildLogOutputList = buildLogDao.getBuildLogOutputsByTeamId(buildLogsInput.getTeamId(), start, pageSize);

        buildLogOutputList.forEach(buildLogOutput -> {
            buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));
            String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
            String branchPath = gitlabUrl + String.format(gitlabBranchPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getBranch());
            buildLogOutput.setCommitPath(commitPath);
            buildLogOutput.setBranchPath(branchPath);
        });

        Integer count = buildLogDao.getBuildLogCountByTeamId(buildLogsInput.getTeamId());
        GetBuildLogOutput buildLogOutputs = new GetBuildLogOutput();
        buildLogOutputs.setBuildLogOutputs(buildLogOutputList);
        buildLogOutputs.setCount(count);

        return ResultOutputUtil.success(buildLogOutputs);
    }

    public ResultOutput getDeployLogOutputTextById(Integer id) {

        String output = buildLogDao.getDeployLogOutputTextById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("output", output);

        return ResultOutputUtil.success(result);
    }

    public ResultOutput getBuildRecentLog(Integer id) {

        BuildLogOutput buildLogOutput = buildLogDao.getBuildRecentLog(id);

        if (buildLogOutput == null || buildLogOutput.getStatus() == null) {
            buildLogOutput = new BuildLogOutput();
            buildLogOutput.setId(id);
            buildLogOutput.setStatus(2);
            buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));
            return ResultOutputUtil.success(buildLogOutput);
        }

        String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
        buildLogOutput.setCommitPath(commitPath);
        buildLogOutput.setStatusText(codeUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));

        return ResultOutputUtil.success(buildLogOutput);
    }

    public ResultOutput getJobsByTeamId(Integer teamId, Integer type) {

        List<JobOutput> jobOutputs = jobDao.getJobOutputsByTeamId(teamId, type);

        return ResultOutputUtil.success(jobOutputs);
    }

    public ResultOutput updateBuildJob(UpdateJobInput updateJobInput) {

        Job job = BeanCopyUtil.copy(updateJobInput, Job.class);
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

        boolean success = jenkinsUtil.updateJob(wholeJob.getJobName(), projectRepository.getPathWithNamespace(), updateJobInput.getScript(), projectRepository.getSshUrl(), guest.getUsername(), member.getApiToken());

        if (!success) {
            throw new TransactionalException(NestStatusCode.UPDATE_JENKINS_JOB_ERROR);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(job, JobOutput.class));
    }

    public ResultOutput updateDeployJob(UpdateDeployInput updateDeployInput) {

        Job job = BeanCopyUtil.copy(updateDeployInput, Job.class);
        Integer count = jobDao.updateDeployJob(job);

        if (count == 0) {
            return ResultOutputUtil.error(NestStatusCode.UPDATE_DEPLOY_JOB_FAILED);
        }

        return ResultOutputUtil.success(BeanCopyUtil.copy(job, JobOutput.class));
    }

    public ResultOutput getDeployLogs(DeployLogInput deployLogInput) {

        Integer maxSize = Integer.parseInt(buildLogSize);
        Integer size = deployLogInput.getSize();
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = deployLogInput.getPage() == 0 ? null : (deployLogInput.getPage() - 1) * pageSize;
        List<DeployLogOutput> deployLogOutputs = deployLogDao.getDeployLogsOutputByTeamId(deployLogInput.getTeamId(), start, size);
        deployLogOutputs.forEach(deployLogOutput -> {
            String commitPath = gitlabUrl + String.format(gitlabCommitPath, deployLogOutput.getCommitPath() == null ? "" : deployLogOutput.getCommitPath(), deployLogOutput.getVersion());
            String branchPath = gitlabUrl + String.format(gitlabBranchPath, deployLogOutput.getCommitPath() == null ? "" : deployLogOutput.getCommitPath(), deployLogOutput.getBranch());
            deployLogOutput.setBranchPath(branchPath);
            deployLogOutput.setCommitPath(commitPath);
        });

        Integer count = deployLogDao.getDeployLogsCount(deployLogInput.getTeamId());

        GetDeployLogOutput getDeployLogOutput = new GetDeployLogOutput();
        getDeployLogOutput.setDeployLogOutputs(deployLogOutputs);
        getDeployLogOutput.setCount(count);

        return ResultOutputUtil.success(getDeployLogOutput);
    }

    public ResultOutput getDeployLogOutputText(Integer id) {

        String output = deployLogDao.getDeployLogOutputTextById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("output", output);

        return ResultOutputUtil.success(result);
    }


    public ResultOutput getProjectDeployConf(Integer projectId, Integer envId) {

        Project project = projectDao.getProjectById(projectId);
        String slug = project.getSlug();
        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        String path = projectRepository.getPathWithNamespace();
        Environment environment = environmentDao.getEnvironmentById(envId);
        String type = codeUtil.getEnumsMessage("environment.type.conf", String.valueOf(environment.getType()));

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

        String conf = String.format(stringBuilder.toString(), slug, slug, path, type, type, type, type, path, slug);
        conf = StringEscapeUtils.unescapeXml(conf);
        Map<String, Object> result = new HashMap<>();
        result.put("conf", conf);

        return ResultOutputUtil.success(result);
    }

    public ResultOutput getProjectDeployScript(Integer projectId, Integer envId) {

        StringBuilder stringBuilder = null;

        try {
            stringBuilder = jenkinsUtil.readFileFromClassPath("/deploy.conf");

        } catch (IOException e) {
            e.printStackTrace();
            return ResultOutputUtil.error(NestStatusCode.GET_DEPLOY_CONF_FAILED);
        }

        String script = stringBuilder.toString();
        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        String path = projectRepository.getPathWithNamespace();
        String prePath = path.substring(0, path.lastIndexOf("/"));
        script = String.format(script, path, prePath);
        script = StringEscapeUtils.unescapeXml(script);
        Map<String, Object> result = new HashMap<>();

        result.put("script", script);

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

        private String operatorName;

        public BuildLogRunnable(Integer buildLogId, String jobName, String account, String operatorName, String passwordOrToken, Integer type, String location, Integer versionType, Integer projectId) {
            this.account = account;
            this.passwordOrToken = passwordOrToken;
            this.type = type;
            this.location = location;
            this.jobName = jobName;
            this.id = buildLogId;
            this.versionType = versionType;
            this.projectId = projectId;
            this.operatorName = operatorName;
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

                updateBuildLog(build, id, jobName, operatorName, location, versionType, projectId);

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

    public BuildLog saveBuildLog(Integer teamId, String jobName, String branch, Integer projectId, Guest guest, Integer status) {

        BuildLog buildLog = new BuildLog();
        buildLog.setTeamId(teamId);
        buildLog.setJobName(jobName);
        buildLog.setBranch(branch);
        buildLog.setProjectId(projectId);
        buildLog.setOperatorId(guest.getId());
        buildLog.setOperatorName(guest.getName());
        buildLog.setStatus(status);
        Integer count = buildLogDao.createBuildLog(buildLog);

        if (count == 0) {
            return null;
        }

        Integer id = buildLog.getId();
        buildLog = buildLogDao.getBuildLogById(id);

        return buildLog;
    }

    public void updateBuildLog(Build build, Integer id, String jobName, String operatorName, String location, Integer versionType, Integer projectId) throws InterruptedException {

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
        buildLog.setOperatorName(operatorName);
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

        if (output.indexOf("tarNameStart") != -1) {
            String tarName = output.substring(output.indexOf("tarNameStart") + 13, output.indexOf("tarNameEnd") - 1);
            buildLog.setTarName(tarName);
        }

        if ("success".equalsIgnoreCase(result)) {
            buildLog.setStatus(1);
        } else {
            buildLog.setStatus(0);
        }

        buildLogDao.updateBuildLog(buildLog);
        buildLog.setProjectId(projectId);
        packageRepositoryService.createPackageRepository(buildLog);
    }
}
