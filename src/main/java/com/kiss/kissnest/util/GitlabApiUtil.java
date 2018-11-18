package com.kiss.kissnest.util;


import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.status.NestStatusCode;
import com.offbytwo.jenkins.JenkinsServer;
import entity.Guest;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import utils.ThreadLocalUtil;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GitlabApiUtil {

    @Value("${gitlab.server.url}")
    private String gitlabServerUrl;

    @Value("${gitlab.server.token.path}")
    private String tokenPath;

    @Value("${kiss.nest.webHook.url}")
    private String webHookUrl;

    public String getAccessToken (String account,String password) throws Exception {

        Map<String,Object> map = new HashMap<>();
        map.put("grant_type","password");
        map.put("username",account);
        map.put("password",password);
        String accessTokenStr = HttpUtil.doPost(gitlabServerUrl + tokenPath,JSONObject.toJSONString(map));
        String accessToken = JSONObject.parseObject(accessTokenStr).getString("access_token");

        return accessToken;
    }

    public GitlabGroup createGroup (String groupName,String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = gitlabAPI.createGroup(groupName);
            return gitlabGroup;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GitlabGroup createSubGroup (String groupName,String accessToken,Integer parentId) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = gitlabAPI.createGroup(groupName,groupName,null,null,null,parentId);
            return gitlabGroup;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteGroup(Integer groupId,String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            gitlabAPI.deleteGroup(groupId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public GitlabProject createProject (String projectName,String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            GitlabProject gitlabProject = gitlabAPI.createProject(projectName);
            return gitlabProject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GitlabProject createProjectForGroup (String projectName,Integer groupId,String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = null;

            try {
                gitlabGroup = gitlabAPI.getGroup(groupId);
            } catch (FileNotFoundException e) {
//                gitlabGroup = createGroup(groupPath,accessToken);
                throw new TransactionalException(NestStatusCode.GROUP_REPOSITORYID_NOT_EXIST);
            }

            GitlabProject gitlabProject = gitlabAPI.createProjectForGroup(projectName,gitlabGroup);
            gitlabAPI.addProjectHook(gitlabProject.getId(),webHookUrl,true,false,true,true,true);

            return gitlabProject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteProject(Integer projectId,String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            gitlabAPI.deleteProject(projectId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GitlabBranch> getBranches (Integer projectId,String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            List<GitlabBranch> gitlabBranches = gitlabAPI.getBranches(projectId);

            return gitlabBranches;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBranchVersion(Integer projectId,String branch,String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            GitlabBranch gitlabBranch = gitlabAPI.getBranch(projectId,branch);

            return gitlabBranch.getCommit().getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) throws Exception {
//        Map<String,Object> map = new HashMap<>();
//        map.put("grant_type","password");
//        map.put("username","xiaoyue");
//        map.put("password","12345678");
//        String resp = HttpUtil.doPost("http://192.168.99.100:32769/oauth/token",JSONObject.toJSONString(map));
//        String token = JSONObject.parseObject(resp).getString("access_token");
//        System.out.println(token);
        GitlabAPI gitlabAPI = GitlabAPI.connect("http://git.kisscloud.io","909e8fec9700ef7978b8b301e32ba9ae6d7294536301d9aaa17e052a23484612",TokenType.ACCESS_TOKEN);
//        GitlabGroup gitlabGroup = gitlabAPI.getGroup("gitApi");
//        GitlabGroup gitlabGroup1 = gitlabAPI.createGroup("gitApi1","gitApi1",null,null,null,gitlabGroup.getId());
//        System.out.println("hello");
//        GitlabAPI gitlabAPI = GitlabAPI.connect("http://192.168.99.100:32769",token,TokenType.ACCESS_TOKEN);

//        GitlabProject gitlabProject = gitlabAPI.getProject(9219426);
//        gitlabAPI.addProjectHook(9219426,"http://10.100.10.11:8920/kiss/nest/note",true,false,true,true,true);
//        System.out.println();
//        List<GitlabBranch> gitlabBranches = gitlabAPI.getBranches(9003472);


//        for (GitlabBranch gitlabBranch : gitlabBranches) {
//            System.out.println(gitlabBranch.getName());
//        }
//        GitlabProject gitlabProject = gitlabAPI.createProject("bis");
//        GitlabBranch gitlabBranch = gitlabAPI.getBranch("2","master");
//        GitlabBranchCommit gitlabBranchCommit = gitlabBranch.getCommit();

//        GitlabProjectHook gitlabProjectHook = gitlabAPI.addProjectHook(2,"http://localhost:8920/kiss/nest/note",true,false,true,true,true);
        GitlabProject gitlabProject = gitlabAPI.getProject(9);
        System.out.println(gitlabProject.getPathWithNamespace());
//        gitlabProject.getId();
//        gitlabProject.getCreatedAt();
//        gitlabProject.getName();
//        gitlabProject.getSshUrl();
//        gitlabProject.getHttpUrl();

        System.out.println("");
    }
}
