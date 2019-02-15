package com.kiss.kissnest.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.enums.RepositoryType;
import com.kiss.kissnest.exception.TransactionalException;
import com.kiss.kissnest.status.NestStatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.kiss.foundation.utils.ThreadLocalUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class GitlabApiUtil {

    @Value("${gitlab.server.url}")
    private String gitlabServerUrl;

    @Value("${gitlab.server.token.path}")
    private String tokenPath;

    @Value("${kiss.nest.webHook.url}")
    private String webHookUrl;

    @Value("${gitlab.server.user}")
    private String gitlabServerUser;

    public String getAccessToken(String account, String password) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("grant_type", "password");
        map.put("username", account);
        map.put("password", password);
        String accessTokenStr = HttpUtil.doPost(gitlabServerUrl + tokenPath, JSONObject.toJSONString(map));

        if (accessTokenStr == null) {
            return null;
        }

        String accessToken = JSONObject.parseObject(accessTokenStr).getString("access_token");

        return accessToken;
    }

    public GitlabGroup createGroup(String groupName, String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = gitlabAPI.createGroup(groupName);
            return gitlabGroup;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GitlabGroup createSubGroup(String groupName, String accessToken, Integer parentId) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = gitlabAPI.createGroup(groupName, groupName, null, null, null, parentId);
            return gitlabGroup;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteGroup(Integer groupId, String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            gitlabAPI.deleteGroup(groupId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public GitlabProject createProject(String projectName, String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            GitlabProject gitlabProject = gitlabAPI.createProject(projectName);
            return gitlabProject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GitlabProject createProjectForGroup(String projectName, Integer groupId, String accessToken) {

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            GitlabGroup gitlabGroup = null;

            try {
                gitlabGroup = gitlabAPI.getGroup(groupId);
            } catch (FileNotFoundException e) {
//                gitlabGroup = createGroup(groupPath,accessToken);
                throw new TransactionalException(NestStatusCode.GROUP_REPOSITORYID_NOT_EXIST);
            }

            GitlabProject gitlabProject = gitlabAPI.createProjectForGroup(projectName, gitlabGroup);
            gitlabAPI.addProjectHook(gitlabProject.getId(), webHookUrl, true, false, true, true, true);

            return gitlabProject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteProject(Integer projectId, String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            gitlabAPI.deleteProject(projectId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GitlabBranch> getBranches(Integer projectId, String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            List<GitlabBranch> gitlabBranches = gitlabAPI.getBranches(projectId);

            return gitlabBranches;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GitlabTag addTag(Integer projectId, String tagName, String ref, String message, String releaseDescription, String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            GitlabTag gitlabTag = gitlabAPI.addTag(projectId, tagName, ref, message, releaseDescription);

            return gitlabTag;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<GitlabTag> getTags(Integer projectId, String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
           return gitlabAPI.getTags(projectId);
        } catch (Exception e) {
            log.info("获取tag失败了");
            e.printStackTrace();
            return null;
        }
    }

    public void addMember(Integer repositoryId, String accessToken, String username, Integer level, RepositoryType type, String name) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            String users = getUser(username, accessToken);

            if (users == null) {
                ThreadLocalUtil.setString("member_account_has_not_been_activated", name);
                throw new TransactionalException(NestStatusCode.MEMBER_ACCOUNT_HAS_NOT_BEEN_ACTIVATED);
            }

            JSONArray jsonArray = JSONObject.parseArray(users);

            if (jsonArray.size() == 0) {
                return;
            }

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Integer id = jsonObject.getInteger("id");

            GitlabAccessLevel accessLevel;

            switch (level) {
                case 1:
                    accessLevel = GitlabAccessLevel.Master;
                    break;
                case 2:
                    accessLevel = GitlabAccessLevel.Developer;
                    break;
                default:
                    accessLevel = GitlabAccessLevel.Guest;
            }

            switch (type) {
                case Group:
                    gitlabAPI.addGroupMember(repositoryId, id, accessLevel);
                    break;
                case SubGroup:
                    gitlabAPI.addGroupMember(repositoryId, id, accessLevel);
                    break;
                case Project:
                    gitlabAPI.addProjectMember(repositoryId, id, accessLevel);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBranchVersion(Integer projectId, String branch, String accessToken) {
        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect(gitlabServerUrl, accessToken, TokenType.ACCESS_TOKEN);
            GitlabBranch gitlabBranch = gitlabAPI.getBranch(projectId, branch);

            return gitlabBranch.getCommit().getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUser(String userName, String accessToken) {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(String.format(gitlabServerUser, userName));
        CloseableHttpResponse response = null;
        try {
            httpGet.setHeader("Authorization", "Bearer " + accessToken);
            response = httpclient.execute(httpGet);
            String str = EntityUtils.toString(response.getEntity(), "utf-8");
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            httpGet.abort();
            return null;
        } finally {
            httpGet.abort();
            if (response != null) {

                try {
                    response.close();
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
//        GitlabAPI gitlabAPI = GitlabAPI.connect("http://git.kisscloud.io","909e8fec9700ef7978b8b301e32ba9ae6d7294536301d9aaa17e052a23484612",TokenType.ACCESS_TOKEN);
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
//        GitlabProject gitlabProject = gitlabAPI.getProject(9);
//        System.out.println(gitlabProject.getPathWithNamespace());
//        GitlabSession gitlabSession = GitlabAPI.connect("http://git.kisscloud.io","xiaoqian","12345678");
//        String token = gitlabSession.getPrivateToken();
//        gitlabProject.getId();
//        gitlabProject.getCreatedAt();
//        gitlabProject.getName();
//        gitlabProject.getSshUrl();
//        gitlabProject.getHttpUrl();

        System.out.println("");

        try {
//            GitlabAPI gitlabAPI = GitlabAPI.connect("http://git.kisscloud.io", "909e8fec9700ef7978b8b301e32ba9ae6d7294536301d9aaa17e052a23484612", TokenType.ACCESS_TOKEN);
//            GitlabAPI gitlabAPI1 = GitlabAPI.connect("http://git.kisscloud.io", "4c372b421f6bbeca7a723c0ac9e7a8c2c004e24dca06274f927c1c77af5fc0d1", TokenType.ACCESS_TOKEN);
//            GitlabUser gitlabUser = gitlabAPI1.getUser();
//            gitlabAPI.addGroupMember(35, gitlabUser.getId(), GitlabAccessLevel.Developer);
//            System.out.println("aaa");
//            GitLabApi gitLabApi = new GitLabApi("http://git.kisscloud.io",Constants.TokenType.ACCESS,"909e8fec9700ef7978b8b301e32ba9ae6d7294536301d9aaa17e052a23484612");
//            User user = gitLabApi.getUserApi().getUser("xiaoqian");
//            CloseableHttpClient httpclient = HttpClients.createDefault();
//            HttpGet httpGet = new HttpGet("http://git.kisscloud.io/api/v4/users?username=xiaoqian");
//            httpGet.setHeader("Authorization","Bearer 909e8fec9700ef7978b8b301e32ba9ae6d7294536301d9aaa17e052a23484612");
//            HttpResponse response = httpclient.execute(httpGet);
//            String str = EntityUtils.toString(response.getEntity(),"utf-8");
//            System.out.println(str);
//            System.out.println(user);
//            List<NameValuePair> params = new ArrayList<>();
//            params.add(new BasicNameValuePair("username","salt-api"));
//            params.add(new BasicNameValuePair("password","12345678"));
//            params.add(new BasicNameValuePair("eauth","pam"));
//            params.add(new BasicNameValuePair("client","local"));
//            params.add(new BasicNameValuePair("tgt","*"));
//            params.add(new BasicNameValuePair("fun","cmd.run"));
//            params.add(new BasicNameValuePair("arg","cd /opt && touch test.txt"));

//            Map<String, Object> params = new HashMap<>();
////            params.put("username","salt-api");
////            params.put("password","12345678");
////            params.put("eauth","pam");
//            params.put("client", "local");
//            params.put("tgt", "*");
//            params.put("fun", "cmd.run");
//            params.put("arg", "cd /opt && touch test.txt");
//            String str = HttpUtil.formDataPost("http://47.100.235.203:8000",params);

//            CloseableHttpClient httpclient = HttpClients.createDefault();
//            HttpPost httpPost = new HttpPost("http://47.100.235.203:8000");// 创建httpPost
//            httpPost.setHeader("Content-Type", "application/json");
//            httpPost.setHeader("Cache-Control", "no-cache");
//            String charSet = "UTF-8";
//            StringEntity entity = new StringEntity(JSONObject.toJSONString(params));
//            httpPost.setEntity(entity);
//            CloseableHttpResponse response = null;

//            response = httpclient.execute(httpPost);
//            System.out.println(str);

//            URL url = new URL("http://47.100.235.203:8000");
//            SaltStackClient saltStackClient = new SaltStackClient(URI.create("http://47.100.235.203:8000"));
//
//            Map<String, String> props = new LinkedHashMap();
//            props.put("username", "salt-api");
//            props.put("password", "12345678");
//            props.put("eauth", AuthModule.PAM.getValue());
////            Gson gson = (new GsonBuilder()).create();
////            String payload = gson.toJson(props);
//            String payload = JSONObject.toJSONString(props);
//            ConnectionFactory connectionFactory = new HttpClientConnectionFactory();
//            ClientConfig config = new ClientConfig();
//            config.put(ClientConfig.URL, new URI("http://47.100.235.203:8000"));
//            Connection connection = connectionFactory.create("/login", JsonParser.TOKEN, config);
//            connection.getResult(payload);
//            Object object = connectionFactory.create("/login", JsonParser.TOKEN, config).getResult(payload);


//            saltStackClient.login("salt-api","12345678",AuthModule.PAM);
//            List<String> list = new ArrayList<>();
//            list.add("cd /opt && touch test.txt");
//            Optional<List<String>> optional = Optional.of(list);
//
//            LocalCall localCall = new LocalCall("cmd.run",optional,null,TypeToken.get(Object.class));
//            saltStackClient.callSync(localCall,new Glob());
//            OkHttpClient client = new OkHttpClient();
////
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType, "{\n    \"username\": \"salt-api\",\n    \"password\": \"12345678\",\n    \"eauth\": \"pam\"\n}");
//            Request request = new Request.Builder()
//                    .url("http://47.100.235.203:8000/login")
//                    .post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            Response response = client.newCall(request).execute();
//
//            String tokenResp = response.body().string();
//            JSONObject jsonObject = JSONObject.parseObject(tokenResp);
//            JSONArray jsonArray =  jsonObject.getJSONArray("return");
////            JSONArray jsonArray = JSONObject.parseArray(jsonObject.getString("return"));
//            JSONObject tokenJson = jsonArray.getJSONObject(0);
//            System.out.println(tokenJson.getString("token"));
//            OkHttpClient client = new OkHttpClient();
////
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(params));
//            Request request = new Request.Builder()
//                    .url("http://47.100.235.203:8000")
//                    .post(body)
//                    .addHeader("Accept", "application/json")
//                    .addHeader("X-Auth-Token", "6c20c994d77ea25946e1a398a58a9df857bbfd8d")
////                    .addHeader("Cache-Control", "no-cache")
////                    .addHeader("Postman-Token", "8c1390fe-d567-49f6-aac8-c72eb60e9870")
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            System.out.println(response.code());
//            System.out.println(response.body());
//            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            GitlabAPI gitlabAPI = GitlabAPI.connect("http://git.kisscloud.io", "988e3ea19084cff69511c1b03a50e92b7c2134a52fe516890674edf55d489dbf", TokenType.ACCESS_TOKEN);
            List<GitlabTag> gitlabTags = gitlabAPI.getTags(3);

            gitlabTags.forEach(gitlabTag -> {
                System.out.println(gitlabTag.getName());
                System.out.println(gitlabTag.getName());
                System.out.println(gitlabTag.getMessage());
                GitlabBranchCommit gitlabBranchCommit = gitlabTag.getCommit();
                GitlabRelease gitlabRelease = gitlabTag.getRelease();

                System.out.println();
            });
        } catch (Exception e) {
            log.info("获取tag失败了");
            e.printStackTrace();
        }
    }
}
