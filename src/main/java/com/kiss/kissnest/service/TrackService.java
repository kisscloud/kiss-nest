package com.kiss.kissnest.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.dao.ProjectDao;
import com.kiss.kissnest.dao.ProjectRepositoryDao;
import com.kiss.kissnest.dao.TrackDao;
import com.kiss.kissnest.enums.OperationTargetType;
import com.kiss.kissnest.entity.Project;
import com.kiss.kissnest.entity.ProjectRepository;
import com.kiss.kissnest.entity.Track;
import com.kiss.kissnest.output.TrackOutput;
import com.kiss.kissnest.util.GitlabApiUtil;
import com.kiss.kissnest.util.ResultOutputUtil;
import entity.Guest;
import org.apache.commons.lang3.StringUtils;
import org.gitlab.api.models.GitlabBranch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import output.ResultOutput;
import utils.BeanCopyUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrackService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private TrackDao trackDao;

    @Autowired
    private ProjectRepositoryDao projectRepositoryDao;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private GitlabApiUtil gitlabApiUtil;

    public void createTrack(String hook) {

        if (StringUtils.isEmpty(hook)) {
            return;
        }

        Track track = new Track();

        JSONObject hookJson = JSONObject.parseObject(hook);
        String eventName = hookJson.getString("object_kind");

        if ("push".equals(eventName)) {
            track.setType(1);
            push(hookJson,track);
            projectRepositoryDao.addCount("commit",1);
        } else if ("tag_push".equals(eventName)) {
            track.setType(2);
            push(hookJson,track);
            projectRepositoryDao.addCount("commit",1);
        } else if ("merge_request".equals(eventName)) {
            track.setType(3);
            merge(hookJson,track);
            projectRepositoryDao.addCount("mergeRequest",1);
        }

        Project project = projectDao.getProjectByRepositoryId(track.getProjectId());

        if (project != null) {
            track.setTeamId(project.getTeamId());
        }

        updateBranch(project.getTeamId(),project.getId());
        trackDao.createTrack(track);
        operationLogService.saveDynamic(new Guest(),track.getTeamId(),null,null,OperationTargetType.TYPE__PUSH_CODES,track);
    }

    public void push(JSONObject hookJson, Track track) {

        track.setRef(hookJson.getString("ref"));
        JSONObject projectJson = hookJson.getJSONObject("project");

        if (projectJson != null) {
            track.setProjectId(projectJson.getInteger("id"));
            track.setProjectName(projectJson.getString("name"));
        }

        JSONArray commitJson = hookJson.getJSONArray("commits");

        if (commitJson != null && commitJson.size() != 0) {
            JSONObject firstCommit = commitJson.getJSONObject(0);

            if (firstCommit != null) {
                track.setHash(firstCommit.getString("id"));
                track.setMessage(firstCommit.getString("message"));
                track.setModified(firstCommit.getString("modified"));
                track.setOperatorAt(com.kiss.kissnest.util.StringUtils.utcStringToDefaultString(firstCommit.getString("timestamp"), "yyyy-MM-dd'T'HH:mm:ss Z"));
                JSONObject auth = firstCommit.getJSONObject("author");
                track.setAuthorName(auth.getString("name"));
                track.setAuthorEmail(auth.getString("email"));
            } else if ("0000000000000000000000000000000000000000".equals(hookJson.getString("before")) && track.getType() == 1) {
                track.setType(3);
                track.setHash(hookJson.getString("after"));
            }
        }

    }

    public void merge(JSONObject hookJson, Track track) {

        JSONObject userJson = hookJson.getJSONObject("user");
        track.setAuthorName(userJson == null ? null : userJson.getString("username"));
        JSONObject projectJson = hookJson.getJSONObject("project");
        track.setProjectId(projectJson == null ? null : projectJson.getInteger("id"));
        track.setProjectName(projectJson == null ? null : projectJson.getString("name"));
        JSONObject attributeJson = hookJson.getJSONObject("object_attributes");

        if (attributeJson != null) {
            track.setOperatorAt(com.kiss.kissnest.util.StringUtils.utcStringToDefaultString(attributeJson.getString("created_at"), "yyyy-MM-dd HH:mm:ss Z"));
            track.setMessage(attributeJson.getString("description"));
            track.setSource(attributeJson.getString("source_branch"));
            track.setTarget(attributeJson.getString("target_branch"));
            track.setTitle(attributeJson.getString("title"));
        }

        JSONObject commitJson = attributeJson.getJSONObject("last_commit");
        track.setHash(commitJson == null ? null : commitJson.getString("id"));
    }

    public ResultOutput getTracksByTeamId(Integer teamId) {

        List<Track> tracks = trackDao.getTracksByTeamId(teamId);
        List<TrackOutput> trackOutputs = new ArrayList<>();

        if (tracks != null) {
            for (Track track : tracks) {
                trackOutputs.add(BeanCopyUtil.copy(track, TrackOutput.class));
            }
        }

        return ResultOutputUtil.success(trackOutputs);
    }

    public void updateBranch(Integer teamId,Integer projectId) {

        String accessToken = projectDao.getProjectOperatorAccessToken(projectId);

        if (StringUtils.isEmpty(accessToken)) {
            return;
        }

        ProjectRepository projectRepository = projectRepositoryDao.getProjectRepositoryByProjectId(projectId);
        List<GitlabBranch> gitlabBranches = gitlabApiUtil.getBranches(projectRepository.getRepositoryId(),accessToken);
        Integer count = gitlabBranches.size();

        projectRepositoryDao.updateProjectRepositoryBranch(teamId,projectId,count);
    }
}
