package com.kiss.kissnest.util;


import com.alibaba.fastjson.JSONObject;
import entity.Guest;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabProject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import utils.ThreadLocalUtil;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Component
public class GitlabApiUtil {

    @Value("${oauth2.server.url}")
    private String gitlabServerUrl;

    @Value("${oauth2.server.token.path}")
    private String tokenPath;

    public String getAccessToken (String password) throws Exception {

        Map<String,Object> map = new HashMap<>();
        Guest guest = ThreadLocalUtil.getGuest();
        map.put("grant_type","password");
        map.put("username",guest.getName());
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

    public GitlabProject createProjectForGroup (String projectName,String groupPath,String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl,accessToken,TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = null;

            try {
                gitlabGroup = gitlabAPI.getGroup(groupPath);
            } catch (FileNotFoundException e) {
                gitlabGroup = createGroup(groupPath,accessToken);
            }

            GitlabProject gitlabProject = gitlabAPI.createProjectForGroup(projectName,gitlabGroup);

            return gitlabProject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("grant_type","password");
        map.put("username","qrl758");
        map.put("password","57zlrschzaxwgm57");
        String resp = HttpUtil.doPost("https://gitlab.com/oauth/token",JSONObject.toJSONString(map));
        String token = JSONObject.parseObject(resp).getString("access_token");
        System.out.println(token);
//        GitlabAPI gitlabAPI = GitlabAPI.connect("https://gitlab.com",token,TokenType.ACCESS_TOKEN);
//        GitlabGroup gitlabGroup = gitlabAPI.getGroup("gitApi");
//        GitlabGroup gitlabGroup1 = gitlabAPI.createGroup("gitApi1","gitApi1",null,null,null,gitlabGroup.getId());
//        System.out.println("hello");
    }
}
