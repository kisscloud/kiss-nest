package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.dao.*;
import com.kiss.kissnest.entity.*;
import com.kiss.kissnest.enums.BuildJobStatusEnums;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.enums.WebSocketMessageTypeEnums;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.input.*;
import com.kiss.kissnest.output.*;
import com.kiss.kissnest.status.NestStatusCode;
import com.kiss.kissnest.util.JenkinsUtil;
import com.kiss.kissnest.util.LangUtil;
import com.kiss.kissnest.util.OutputUtil;
import com.kiss.kissnest.util.SaltStackUtil;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import com.kiss.foundation.entity.Guest;
import com.kiss.foundation.exception.StatusException;
import com.kiss.foundation.utils.BeanCopyUtil;
import com.kiss.foundation.utils.GuestUtil;
import com.kiss.foundation.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private LangUtil langUtil;

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

    @Value("${jenkins.configurePath}")
    private String jenkinsConfigurePath;

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.notificationUrl}")
    private String notificationUrl;

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
    private DeployNodeLogDao deployNodeLogDao;

    @Autowired
    private SaltStackUtil saltStackUtil;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private OutputUtil outputUtil;

    public JobOutput createBuildJob(CreateJobInput createJobInput) {

        Integer projectId = createJobInput.getProjectId();
        Project project = projectDao.getProjectById(projectId);

        if (StringUtils.isEmpty(project.getSlug())) {
            throw new StatusException(NestStatusCode.PROJECT_SLUG_EMPTY);
        }

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (StringUtils.isEmpty(member.getApiToken())) {
            throw new StatusException(NestStatusCode.MEMBER_APITOKEN_IS_EMPTY);
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);

        String jobName = projectRepository.getPathWithNamespace().replaceAll("/", "-");
        boolean success = jenkinsUtil.createJobByShell(jobName, projectRepository.getPathWithNamespace(), createJobInput.getScript(), notificationUrl, projectRepository.getSshUrl(), guest.getUsername(), createJobInput.getWorkspace(), member.getApiToken());

        if (!success) {
            throw new TransactionalException(NestStatusCode.CREATE_JENKINS_JOB_ERROR);
        }

        Job job = new Job();
        job.setTeamId(project.getTeamId());
        job.setJobName(jobName);
        job.setProjectId(projectId);
        job.setScript(createJobInput.getScript());
        job.setType(createJobInput.getType());
        job.setStatus(0);
        job.setNumber(0);
        job.setWorkspace(createJobInput.getWorkspace());
        job.setJobUrl(String.format(jenkinsUrl + jenkinsConfigurePath, jobName));
        jobDao.createJob(job);

        JobOutput jobOutput = jobDao.getJobOutputsById(job.getId());

        operationLogService.saveOperationLog(project.getTeamId(), guest, null, job, "id", OperationTargetType.TYPE__CREATE_BUILD_JOB);
        operationLogService.saveDynamic(guest, job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__CREATE_BUILD_JOB, job);

        return jobOutput;
    }

    public JobOutput createDeployJob(CreateDeployInput createDeployInput) {

        Integer projectId = createDeployInput.getProjectId();
        Project project = projectDao.getProjectById(projectId);

        if (StringUtils.isEmpty(project.getSlug())) {
            throw new StatusException(NestStatusCode.PROJECT_SLUG_EMPTY);
        }

        Job exist = jobDao.getDeployJobByProjectIdAndEnvId(projectId, createDeployInput.getEnvId());

        if (exist != null) {
            throw new StatusException(NestStatusCode.DEPLOY_JOB_IS_EXIST);
        }

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (StringUtils.isEmpty(member.getApiToken())) {
            throw new StatusException(NestStatusCode.MEMBER_APITOKEN_IS_EMPTY);
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        String jobName = projectRepository.getPathWithNamespace().replaceAll("/", "-");

        List<Integer> serverIdList = createDeployInput.getServerIds();
        Job job = new Job();
        job.setTeamId(project.getTeamId());
        job.setJobName(jobName);
        job.setProjectId(projectId);
        job.setUseSupervisor(createDeployInput.getUseSupervisor());
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

        return jobOutput;
    }

    @Transactional
    public BuildLog buildJob(BuildJobInput buildJobInput) {

        List<Job> jobs = jobDao.getJobByProjectIdAndType(buildJobInput.getProjectId(), 1);
        Job job = jobs.size() > 0 ? jobs.get(0) : null;

        if (job == null) {
            throw new StatusException(NestStatusCode.JOB_NOT_EXIST);
        }

        Project project = projectDao.getProjectById(job.getProjectId());
        String jobName = job.getJobName();

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        String location = jenkinsUtil.buildJob(jobName, buildJobInput.getBranch(), guest.getUsername(), member.getApiToken());

        if (location == null) {
            throw new TransactionalException(NestStatusCode.BUILD_JENKINS_JOB_ERROR);
        }

        Pattern queueIdPattern = Pattern.compile(".*/queue/item/([0-9]+)");
        Matcher queueIdMatcher = queueIdPattern.matcher(location);
        Long queueId = null;

        if (queueIdMatcher.find() && queueIdMatcher.group(1) != null) {
            queueId = Long.parseLong(queueIdMatcher.group(1));
        }

        BuildLog buildLog = saveBuildLog(job.getTeamId(), jobName, buildJobInput.getBranch(), buildJobInput.getProjectId(), guest, queueId, BuildJobStatusEnums.PENDING.value());

        if (buildLog == null) {
            throw new StatusException(NestStatusCode.CREATE_BUILD_LOG_FAILED);
        }

        operationLogService.saveOperationLog(job.getTeamId(), guest, job, null, "id", OperationTargetType.TYPE__BUILD_JOB);
        operationLogService.saveDynamic(guest, job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__BUILD_JOB, job);


        return buildLog;
    }


    public HashMap checkProgram(Integer projectId, Integer envId) {

        Job job = jobDao.getDeployJobByProjectIdAndEnvId(projectId, envId);
        Environment environment = environmentDao.getEnvironmentById(job.getEnvId());
        List<Integer> serverIds = JSONObject.parseArray(job.getServerIds(), Integer.class);

        String targetIps = serverDao.getServerIpsByIds(serverIds);

        String command = String.format("supervisorctl status %s", job.getJobName());

        String response = saltStackUtil.callLocalSync(environment.getSaltHost(), environment.getSaltUser(), environment.getSaltPassword(), environment.getSaltVersion(), "cmd.run", targetIps, command);

        JSONObject returnJson = JSONObject.parseObject(response);
        JSONArray states = returnJson.getJSONArray("return");
        HashMap<String, String> result = new HashMap<>();

        for (int i = 0; i < states.size(); i++) {
            JSONObject state = states.getJSONObject(i);
            for (String key : state.keySet()) {

                if (state.getString(key).contains("no such process")) {
                    result.put(key, "NONE");
                } else if (state.getString(key).contains("STOPPED")) {
                    result.put(key, "STOPPED");
                } else if (state.getString(key).contains("RUNNING")) {
                    result.put(key, "RUNNING");
                }
            }
        }

        return result;
    }

    public HashMap startProgram(Integer projectId, Integer envId) {

        Job job = jobDao.getDeployJobByProjectIdAndEnvId(projectId, envId);
        Environment environment = environmentDao.getEnvironmentById(job.getEnvId());
        List<Integer> serverIds = JSONObject.parseArray(job.getServerIds(), Integer.class);
        String targetIps = serverDao.getServerIpsByIds(serverIds);

        String command = String.format("supervisorctl start %s", job.getJobName());

        String response = saltStackUtil.callLocalSync(environment.getSaltHost(), environment.getSaltUser(), environment.getSaltPassword(), environment.getSaltVersion(), "cmd.run", targetIps, command);
        JSONObject returnJson = JSONObject.parseObject(response);
        JSONArray states = returnJson.getJSONArray("return");
        HashMap<String, String> result = new HashMap<>();

        for (int i = 0; i < states.size(); i++) {
            JSONObject state = states.getJSONObject(i);
            for (String key : state.keySet()) {

                if (state.getString(key).contains("no such process")) {
                    result.put(key, "NONE");
                } else if (state.getString(key).contains("STOPPED")) {
                    result.put(key, "STOPPED");
                }
            }
        }

        return result;
    }

    public HashMap restartProgram(Integer projectId, Integer envId) {
        Job job = jobDao.getDeployJobByProjectIdAndEnvId(projectId, envId);
        Environment environment = environmentDao.getEnvironmentById(job.getEnvId());
        List<Integer> serverIds = JSONObject.parseArray(job.getServerIds(), Integer.class);
        String targetIps = serverDao.getServerIpsByIds(serverIds);

        String command = String.format("supervisorctl restart %s", job.getJobName());

        String response = saltStackUtil.callLocalSync(environment.getSaltHost(), environment.getSaltUser(), environment.getSaltPassword(), environment.getSaltVersion(), "cmd.run", targetIps, command);
        JSONObject returnJson = JSONObject.parseObject(response);
        JSONArray states = returnJson.getJSONArray("return");
        HashMap<String, String> result = new HashMap<>();

        for (int i = 0; i < states.size(); i++) {
            JSONObject state = states.getJSONObject(i);
            for (String key : state.keySet()) {

                if (state.getString(key).contains("no such process")) {
                    result.put(key, "NONE");
                } else if (state.getString(key).contains("STOPPED")) {
                    result.put(key, "STOPPED");
                }
            }
        }
        return result;
    }

    public HashMap stopProgram(Integer projectId, Integer envId) {
        Job job = jobDao.getDeployJobByProjectIdAndEnvId(projectId, envId);
        Environment environment = environmentDao.getEnvironmentById(job.getEnvId());
        List<Integer> serverIds = JSONObject.parseArray(job.getServerIds(), Integer.class);
        String targetIps = serverDao.getServerIpsByIds(serverIds);

        String command = String.format("supervisorctl stop %s", job.getJobName());

        String response = saltStackUtil.callLocalSync(environment.getSaltHost(), environment.getSaltUser(), environment.getSaltPassword(), environment.getSaltVersion(), "cmd.run", targetIps, command);
        JSONObject returnJson = JSONObject.parseObject(response);
        JSONArray states = returnJson.getJSONArray("return");
        HashMap<String, String> result = new HashMap<>();

        for (int i = 0; i < states.size(); i++) {
            JSONObject state = states.getJSONObject(i);
            for (String key : state.keySet()) {

                if (state.getString(key).contains("no such process")) {
                    result.put(key, "NONE");
                } else if (state.getString(key).contains("STOPPED")) {
                    result.put(key, "STOPPED");
                }
            }
        }
        return result;
    }

    public DeployLogOutput deployJob(DeployJobInput deployJobInput) {

        Job job = jobDao.getDeployJobByProjectIdAndEnvId(deployJobInput.getProjectId(), deployJobInput.getEnvId());
        Environment environment = environmentDao.getEnvironmentById(job.getEnvId());
        Integer type = environment.getType();
        String tarName;
        String jarName;
        String version;

        //测试环境
        if (type == 1) {
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

        if (StringUtils.isEmpty(tarName) || StringUtils.isEmpty(jarName) || StringUtils.isEmpty(version)) {
            throw new StatusException(NestStatusCode.JOB_DEPLOY_PACKAGE_LOSE);
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(deployJobInput.getProjectId());
        String path = projectRepository.getPathWithNamespace();

        String script = job.getScript();

        script = script.replace("__TAR__PACKAGE__", packageUrl + path + "/" + tarName);
        String slug = path.replace("/", "-");
        path = path.substring(0, path.lastIndexOf("/") + 1);
        script = script.replace("__CONFIG__", configUrl + ":" + path + "config.git");

        String command = "mkdir -p /opt/scripts/" + path + " && cd /opt/scripts/" + path + " && echo '" + script + "' > " + deployJobInput.getProjectId() + ".sh && chmod +x " + deployJobInput.getProjectId() + ".sh"
                + " && ./" + deployJobInput.getProjectId() + ".sh";

        String conf = "";

        if (job.getUseSupervisor()) {
            conf = job.getConf();
            conf = conf.replace("__BIN__", jarName);
            conf = conf.replace("__CONFIG__", "/opt/configs/" + path + "config");
            command = command + " && cd /etc/supervisor/conf.d && echo '" + conf + "' > " + slug + ".conf ";
        } else {
            command = command + " && echo $? ";
        }

        List<Integer> serverIds = JSONObject.parseArray(job.getServerIds(), Integer.class);
        String targetIps = serverDao.getServerIpsByIds(serverIds);
        String[] deployNodes = targetIps.split(",");

        log.info("操作{},目标{},conf{}", command, targetIps, conf);

        HashMap programStates = checkProgram(deployJobInput.getProjectId(), deployJobInput.getEnvId());
        String runCommand;
        List<Future<String>> tasks = new ArrayList<>();

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
        deployLog.setOperatorId(GuestUtil.getGuestId());
        deployLog.setOperatorName(GuestUtil.getName());
        deployLog.setTotalTasks(tasks.size());
        deployLogDao.createDeployLog(deployLog);

        for (String deployNode : deployNodes) {
            String nodeStatus = (String) programStates.get(deployNode);
            if (nodeStatus.equals("NONE")) {
                runCommand = command + "&& supervisorctl reread && supervisorctl update " + slug;
            } else if (nodeStatus == null || nodeStatus.equals("STOPPED")) {
                runCommand = command + "&& supervisorctl reread && supervisorctl start " + slug;
            } else {
                runCommand = command + "&& supervisorctl reread && supervisorctl restart " + slug;
            }
            runCommand = runCommand + " && echo $? ";
            tasks.add(execDeployCommand(deployLog, environment, deployNode, runCommand));
        }

        while (true) {
            Integer count = 0;
            for (Future<String> task : tasks) {
                if (task.isDone()) {
                    count++;
                }
            }
            if (count.equals(tasks.size())) {
                break;
            }
        }

        log.info("结束了");
//        log.info("部署日志:{}", response);
//
//        Integer success = 0;
//        Integer total = 0;
//
//        if (!StringUtils.isEmpty(response)) {
//            JSONObject returnJson = JSONObject.parseObject(response);
//            JSONArray returnArray = returnJson.getJSONArray("return");
//            JSONObject node = returnArray.getJSONObject(0);
//            String[] target = targetIps.split(",");
//
//            for (int i = 0; i < target.length; i++) {
//                String message = node.getString(target[i]);
//                System.out.println(message);
//                System.out.println(message.lastIndexOf("\n"));
//                String status = message.substring(message.lastIndexOf("\n") + 1);
//                log.info("status:{}", status);
//
//                if (status.equals("0")) {
//                    success++;
//                }
//
//                total++;
//            }
//        }
//

//        deployLog.setTeamId(job.getTeamId());
//        deployLog.setJobId(job.getId());
//        deployLog.setEnvId(environment.getId());
//        deployLog.setBranch(deployJobInput.getBranch());
//        deployLog.setTag(deployJobInput.getTag());
//        deployLog.setVersion(version);
//        deployLog.setProjectId(job.getProjectId());
//        deployLog.setRemark(deployJobInput.getRemark());
//        deployLog.setStatus(1);
//        deployLog.setStatusText(success + "/" + total);
//        deployLog.setOutput(response);
//        deployLog.setOperatorId(GuestUtil.getGuestId());
//        deployLog.setOperatorName(GuestUtil.getName());
//        deployLogDao.createDeployLog(deployLog);

        DeployLogOutput deployLogOutput = deployLogDao.getDeployLogOutputById(deployLog.getId());
        String commitPath = gitlabUrl + String.format(gitlabCommitPath, deployLogOutput.getCommitPath() == null ? "" : deployLogOutput.getCommitPath(), deployLogOutput.getVersion());
        String branchPath = gitlabUrl + String.format(gitlabBranchPath, deployLogOutput.getCommitPath() == null ? "" : deployLogOutput.getCommitPath(), deployLogOutput.getBranch());

        deployLogOutput.setCommitPath(commitPath);
        deployLogOutput.setBranchPath(branchPath);
        deployLogOutput.setServerIds("[" + targetIps + "]");

        operationLogService.saveOperationLog(job.getTeamId(), ThreadLocalUtil.getGuest(), null, deployLog, "id", OperationTargetType.TYPE__DEPLOY_JOB);
        operationLogService.saveDynamic(ThreadLocalUtil.getGuest(), job.getTeamId(), null, job.getProjectId(), OperationTargetType.TYPE__DEPLOY_JOB, deployLogOutput);

        updateProjectLastDeploy(job.getProjectId(), deployJobInput.getBranch(), deployJobInput.getTag(), version);
        return new DeployLogOutput();
    }

    @Async
    public Future<String> execDeployCommand(DeployLog deployLog, Environment environment, String deployNode, String runCommand) {


        String response = saltStackUtil.callLocalSync(environment.getSaltHost(), environment.getSaltUser(), environment.getSaltPassword(), environment.getSaltVersion(), "cmd.run", deployNode, runCommand);
        log.info("部署日志:{}", response);
        DeployNodeLog deployNodeLog = new DeployNodeLog();
        deployNodeLog.setDeployLogId(deployLog.getId());
        deployNodeLog.setOutput(response);
        deployNodeLogDao.createDeployNodeLog(deployNodeLog);

        if (!StringUtils.isEmpty(response)) {
            JSONObject returnJson = JSONObject.parseObject(response);
            JSONArray returnArray = returnJson.getJSONArray("return");
            JSONObject node = returnArray.getJSONObject(0);

            String message = node.getString(deployNode);
            System.out.println(message);
            System.out.println(message.lastIndexOf("\n"));
            String status = message.substring(message.lastIndexOf("\n") + 1);
            log.info("status:{}", status);

            if (status.equals("0")) {
                deployLogDao.incrementDeployLogSuccessTasks(deployLog.getId());
            }
        }

        return new AsyncResult<>("done");
    }


    public Map<String, Boolean> validateJobExist(Integer projectId, Integer type) {

        Map<String, Boolean> result = new HashMap<>();
        List<Job> jobs = jobDao.getJobByProjectIdAndType(projectId, type);

        if (jobs.size() == 0) {
            result.put("exist", false);
        } else {
            result.put("exist", true);
        }

        return result;
    }

    public GetBuildLogOutput getBuildLogsByTeamId(BuildLogInput buildLogsInput) {

        Integer maxSize = Integer.parseInt(buildLogSize);
        Integer size = buildLogsInput.getSize();
        Integer pageSize = (StringUtils.isEmpty(size) || size > maxSize) ? maxSize : size;
        Integer start = buildLogsInput.getPage() == 0 ? null : (buildLogsInput.getPage() - 1) * pageSize;
        List<BuildLogOutput> buildLogOutputList = buildLogDao.getBuildLogOutputsByTeamId(buildLogsInput.getTeamId(), start, pageSize);

        buildLogOutputList.forEach(buildLogOutput -> {
            buildLogOutput.setStatusText(langUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));
            String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
            String branchPath = gitlabUrl + String.format(gitlabBranchPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getBranch());
            buildLogOutput.setCommitPath(commitPath);
            buildLogOutput.setBranchPath(branchPath);
        });

        Integer count = buildLogDao.getBuildLogCountByTeamId(buildLogsInput.getTeamId());
        GetBuildLogOutput buildLogOutputs = new GetBuildLogOutput();
        buildLogOutputs.setBuildLogOutputs(buildLogOutputList);
        buildLogOutputs.setCount(count);

        return buildLogOutputs;
    }

    public Map<String, Object> getDeployLogOutputTextById(Integer id) {

        String output = buildLogDao.getDeployLogOutputTextById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("output", output);

        return result;
    }

    public BuildLogOutput getBuildRecentLog(Integer id) {

        BuildLogOutput buildLogOutput = buildLogDao.getBuildRecentLog(id);

        if (buildLogOutput == null || buildLogOutput.getStatus() == null) {
            buildLogOutput = new BuildLogOutput();
            buildLogOutput.setId(id);
            buildLogOutput.setStatus(2);
            buildLogOutput.setStatusText(langUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));
            return buildLogOutput;
        }

        String commitPath = gitlabUrl + String.format(gitlabCommitPath, buildLogOutput.getCommitPath() == null ? "" : buildLogOutput.getCommitPath(), buildLogOutput.getVersion());
        buildLogOutput.setCommitPath(commitPath);
        buildLogOutput.setStatusText(langUtil.getEnumsMessage("build.status", String.valueOf(buildLogOutput.getStatus())));

        return buildLogOutput;
    }

    public List<JobOutput> getJobsByTeamId(Integer teamId, Integer type) {

        List<JobOutput> jobOutputs = jobDao.getJobOutputsByTeamId(teamId, type);

        return jobOutputs;
    }

    public JobOutput updateBuildJob(UpdateJobInput updateJobInput) {

        Job job = BeanCopyUtil.copy(updateJobInput, Job.class);
        Integer count = jobDao.updateBuildJob(job);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_JOB_FAILED);
        }

        Job wholeJob = jobDao.getJobById(job.getId());
        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(job.getProjectId());

        Guest guest = ThreadLocalUtil.getGuest();
        Member member = memberDao.getMemberByAccountId(guest.getId());

        if (StringUtils.isEmpty(member.getApiToken())) {
            throw new StatusException(NestStatusCode.MEMBER_APITOKEN_IS_EMPTY);
        }

        boolean success = jenkinsUtil.updateJob(wholeJob.getJobName(), projectRepository.getPathWithNamespace(), updateJobInput.getScript(), notificationUrl, projectRepository.getSshUrl(), guest.getUsername(), updateJobInput.getWorkspace(), member.getApiToken());

        if (!success) {
            throw new TransactionalException(NestStatusCode.UPDATE_JENKINS_JOB_ERROR);
        }

        return BeanCopyUtil.copy(job, JobOutput.class);
    }

    public JobOutput updateDeployJob(UpdateDeployInput updateDeployInput) {

        Job job = BeanCopyUtil.copy(updateDeployInput, Job.class);
        job.setServerIds(updateDeployInput.getServerIds() == null ? null : JSON.toJSONString(updateDeployInput.getServerIds()));
        Integer count = jobDao.updateDeployJob(job);

        if (count == 0) {
            throw new StatusException(NestStatusCode.UPDATE_DEPLOY_JOB_FAILED);
        }

        return BeanCopyUtil.copy(job, JobOutput.class);
    }

    public List<EnvironmentOutput> getDeployEnvs(Integer projectId) {

        List<Environment> environments = environmentDao.getEnvironmentsByProjectId(projectId);
        List<EnvironmentOutput> environmentOutputs = new LinkedList<>();
        for (Environment environment : environments) {
            EnvironmentOutput environmentOutput = new EnvironmentOutput();
            BeanUtils.copyProperties(environment, environmentOutput);
            environmentOutputs.add(environmentOutput);
        }

        return environmentOutputs;
    }

    public GetDeployLogOutput getDeployLogs(DeployLogInput deployLogInput) {

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

        return getDeployLogOutput;
    }

    public Map<String, Object> getDeployLogOutputText(Integer id) {

        String output = deployLogDao.getDeployLogOutputTextById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("output", output);

        return result;
    }


    public Map<String, Object> getProjectDeployConf(Integer projectId, Integer envId) {

        Project project = projectDao.getProjectById(projectId);
        String slug = project.getSlug();
        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        String path = projectRepository.getPathWithNamespace();
        Environment environment = environmentDao.getEnvironmentById(envId);
        String type = langUtil.getEnumsMessage("environment.type.conf", String.valueOf(environment.getType()));

        if (project == null) {
            throw new StatusException(NestStatusCode.PROJECT_NOT_EXIST);
        }

        StringBuilder stringBuilder = null;

        try {
            stringBuilder = jenkinsUtil.readFileFromClassPath("/supervisor.tpl");

        } catch (IOException e) {
            e.printStackTrace();
            throw new StatusException(NestStatusCode.GET_DEPLOY_CONF_FAILED);
        }

        String name = path.replaceAll("/", "-");

        String conf = String.format(stringBuilder.toString(), name, name, path, type, type, type, type, path, slug);
        conf = StringEscapeUtils.unescapeXml(conf);
        Map<String, Object> result = new HashMap<>();
        result.put("conf", conf);

        return result;
    }

    public Map<String, Object> getProjectDeployScript(Integer projectId, Integer envId) {

        StringBuilder stringBuilder = null;

        try {
            stringBuilder = jenkinsUtil.readFileFromClassPath("/deploy-script.tpl");

        } catch (IOException e) {
            e.printStackTrace();
            throw new StatusException(NestStatusCode.GET_DEPLOY_CONF_FAILED);
        }

        String script = stringBuilder.toString();
        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        String path = projectRepository.getPathWithNamespace();
        String prePath = path.substring(0, path.indexOf("/"));
        String sufPath;

        switch (envId) {
            case 2:
                sufPath = "config-server-staging";
                break;
            case 3:
                sufPath = "config-server-production";
                break;
            default:
                sufPath = "config-server-test";
        }

        String sshUrl = projectRepository.getSshUrl();
        String preSshUrl = sshUrl.substring(0, sshUrl.indexOf("/"));

        script = String.format(script, path, prePath, sufPath, preSshUrl);
        script = StringEscapeUtils.unescapeXml(script);
        Map<String, Object> result = new HashMap<>();

        result.put("script", script);

        return result;
    }

    public Map<String, Integer> getPendingJobCount() {

        Integer buildLogCount = buildLogDao.getPendingBuildLogCount();
        Map<String, Integer> result = new HashMap<>();
        result.put("buildingJobs", buildLogCount);

        return result;
    }

    public Job getBuildJobByProjectId(Integer projectId) {

        return jobDao.getBuildJobByProjectId(projectId);
    }

    public Job getDeployJobByProjectId(Integer projectId, Integer envId) {

        return jobDao.getDeployJobByProjectIdAndEnvId(projectId, envId);
    }

    private BuildLog saveBuildLog(Integer teamId, String jobName, String branch, Integer projectId, Guest guest, Long queueId, Integer status) {

        BuildLog buildLog = new BuildLog();
        buildLog.setTeamId(teamId);
        buildLog.setJobName(jobName);
        buildLog.setBranch(branch);
        buildLog.setProjectId(projectId);
        buildLog.setOperatorId(guest.getId());
        buildLog.setOperatorName(guest.getName());
        buildLog.setStatus(status);
        buildLog.setQueueId(queueId);

        Integer count = buildLogDao.createBuildLog(buildLog);

        if (count == 0) {
            return null;
        }

        Integer id = buildLog.getId();
        buildLog = buildLogDao.getBuildLogById(id);

        return buildLog;
    }


    public void buildJobQueued(String jobName, Integer queueId, Integer number, String jobUrl, String branch, String version) {

        BuildLog buildLog = buildLogDao.getBuildLogByJobNameAndQueueId(jobName, queueId);

        if (buildLog != null) {
            buildLog.setLogUrl(jobUrl + "/console");
            buildLog.setStatus(BuildJobStatusEnums.QUEUEING.value());
            buildLog.setNumber(number);
            buildLog.setBranch(branch);
            buildLog.setVersion(version);
            buildLogDao.updateBuildLog(buildLog);

            BuildLogOutput buildLogOutput = outputUtil.toBuildLogOutput(buildLog);
            webSocketService.sendMessage(WebSocketMessageTypeEnums.BUILD_PROJECT_QUEUEING.value(), buildLogOutput);
        }
    }

    public void buildJobStarted(String jobName, Integer queueId) {
        BuildLog buildLog = buildLogDao.getBuildLogByJobNameAndQueueId(jobName, queueId);
        if (buildLog != null) {
            buildLog.setStatus(BuildJobStatusEnums.BUILDING.value());
            buildLog.setBuildAt(System.currentTimeMillis() / 1000);
            buildLogDao.updateBuildLog(buildLog);
            BuildLogOutput buildLogOutput = outputUtil.toBuildLogOutput(buildLog);
            webSocketService.sendMessage(WebSocketMessageTypeEnums.BUILD_PROJECT_START.value(), buildLogOutput);
        }
    }

    public void buildJobFinalized(String jobName, Integer queueId, String status, String jobUrl) {
        BuildLog buildLog = buildLogDao.getBuildLogByJobNameAndQueueId(jobName, queueId);
        if (buildLog != null) {
            buildLog.setDuration(System.currentTimeMillis() / 1000 - buildLog.getBuildAt());
            if (status != null && status.equals("SUCCESS")) {
                buildLog.setStatus(BuildJobStatusEnums.SUCCESS.value());
                buildJobSuccess(buildLog, jobUrl);
            } else {
                buildLog.setStatus(BuildJobStatusEnums.FAILED.value());
                buildLogDao.updateBuildLog(buildLog);
            }
            BuildLogOutput buildLogOutput = outputUtil.toBuildLogOutput(buildLog);
            webSocketService.sendMessage(WebSocketMessageTypeEnums.BUILD_PROJECT_END.value(), buildLogOutput);
        }
    }

    private void buildJobSuccess(BuildLog buildLog, String jobUrl) {

        Build build = new Build(buildLog.getNumber(), jobUrl);

        JenkinsHttpConnection client = build.getClient();

        String output = jenkinsUtil.getConsoleOutputText(client, jobUrl + jenkinsBuildOutputPath);

        if (output.contains("versionStart")) {
            String version = output.substring(output.indexOf("versionStart") + 13, output.indexOf("versionEnd") - 1);
            buildLog.setVersion(version);
        }

        if (output.contains("jarNameStart")) {
            String jarName = output.substring(output.indexOf("jarNameStart") + 13, output.indexOf("jarNameEnd") - 1);
            buildLog.setJarName(jarName);
        }

        if (output.contains("tarNameStart")) {
            String tarName = output.substring(output.indexOf("tarNameStart") + 13, output.indexOf("tarNameEnd") - 1);
            buildLog.setTarName(tarName);
        }

        buildLogDao.updateBuildLog(buildLog);
        packageRepositoryService.createPackageRepository(buildLog);

        updateProjectLastBuild(buildLog.getProjectId(), buildLog.getVersion());

    }

    public void updateProjectLastBuild(Integer projectId, String lastBuild) {

        projectDao.updateLastBuild(projectId, lastBuild);
    }

    public void updateProjectLastDeploy(Integer projectId, String branch, String tag, String version) {

        String lastDeploy = "";

        if (!StringUtils.isEmpty(branch)) {
            lastDeploy = version;
        } else if (!StringUtils.isEmpty(tag)) {
            lastDeploy = tag;
        }

        if (!StringUtils.isEmpty(lastDeploy)) {
            projectDao.updateLastDeploy(projectId, lastDeploy);
        }
    }
}
