package com.kiss.kissnest.util;

import com.alibaba.fastjson.JSONObject;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.*;
import entity.Guest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import utils.ThreadLocalUtil;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JenkinsUtil {

//    @Value("${jenkins.account}")
//    private String account;
//
//    @Value("${jenkins.password}")
//    private String password;

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.buildWithParameterUrl}")
    private String buildWithParameterUrl;

    @Value("${jenkins.buildUrl}")
    private String buildUrl;

    @Value("${jenkins.generateTokenUrl}")
    private String generateTokenUrl;

    public boolean createJob (String jobName,String configPath,String account,String passwordOrToken) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl),account,passwordOrToken);
            StringBuilder builder = readFile(configPath);
            if (builder == null) {
                return false;
            }

            server.createJob(jobName,builder.toString(),false);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

    public boolean createJobByShell (String jobName,String shell,String account,String passwordOrToken) {
        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl),account,passwordOrToken);
            StringBuilder builder = readFileFromClassPath();
            String script = String.format(builder.toString(),shell);

            if (builder == null) {
                return false;
            }

            server.createJob(jobName,script,false);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

    public boolean buildJob (String jobName,String branch,String account,String passwordOrToken) {

        try {
            Map<String,String> params = new HashMap<>();
            String url = String.format(buildUrl,jobName);
            if (branch != null) {
                params.put("branch","master");
                url = String.format(buildWithParameterUrl,jobName);
            }

            String auth = authorizationExecute(params,url,account,passwordOrToken);
            System.out.println(auth);
            return auth == null ? false:true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateApiToken(String account,String password) {

        String url = String.format(generateTokenUrl,account);

        try {
            String result = authorizationExecute(null,url,account,password);

            return JSONObject.parseObject(result).getJSONObject("data").getString("tokenValue");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public StringBuilder readFile (String configPath) {
        BufferedReader bufferedReader = null;
        try {
            StringBuilder builder = new StringBuilder();
            File file = new File(configPath);
            FileInputStream in = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(reader);
            String lineTxt = null;

            while ((lineTxt = bufferedReader.readLine()) != null) {
                builder.append(lineTxt);
            }

            return builder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BuildWithDetails getLastBuildWithDetail(String jobName,String account,String passwordOrToken) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl),account,passwordOrToken);


            FolderJob folderJob = new FolderJob(jobName,jenkinsUrl);
//            folderJob.createFolder("http://localhost:8060");
            JobWithDetails jobWithDetails = server.getJob(folderJob,jobName);

            JenkinsHttpConnection jenkinsHttpConnection = jobWithDetails.getClient();


//            Build build = jobWithDetails.getLastBuild();
//            URL url = new URL(build.getUrl());
//
//            String requestUrl = "http://localhost:8060" + url.getPath().replace("jenkins/","");
//
//            Build build1 = new Build(build.getNumber(),requestUrl);
//            build1.setClient(build.getClient());
//            System.out.println("=====" + build.getNumber());
//
//
//            BuildWithDetails buildWithDetails = build1.details();
//            BuildResult buildResult = buildWithDetails.getResult();
//            Map<String,String> map = buildWithDetails.getParameters();
//           JenkinsHttpConnection client =  buildWithDetails.getClient();
//
//
//
//            System.out.println(client.get(requestUrl + "/logText/progressiveHtml"));

            Build build = jobWithDetails.getBuildByNumber(100);

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

    public JenkinsServer getJenkinsServer (String account,String passwordOrToken) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl),account,passwordOrToken);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return server;
        }
    }

    public Build getBuild (String jobName,JenkinsServer server,Integer number) {

        try {
            JobWithDetails jobWithDetails = server.getJob(jobName);
            Build build = jobWithDetails.getBuildByNumber(number);

            return build;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Build getLastBuild (String jobName,String account,String passwordOrToken) {
        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl),account,passwordOrToken);
            JobWithDetails jobWithDetails = server.getJob(jobName);
            Build build = jobWithDetails.getLastBuild();

            return build;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

    public BuildWithDetails getLastBuildWithDetail(JenkinsHttpConnection client,String url) {

        try {
            BuildWithDetails buildWithDetails = client.get(url,BuildWithDetails.class);
            return buildWithDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getConsoleOutputText(JenkinsHttpConnection client,String url) {

        try {
            return client.get(url);
        } catch (Exception e) {
            return "";
        }
    }

    public void close(JenkinsServer server) {

        if (server != null) {
            server.close();
        }
    }

    public StringBuilder readFileFromClassPath () throws IOException {

        InputStream in = JenkinsUtil.class.getResourceAsStream("/config.xml");
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            builder.append(lineTxt);
        }

        return builder;
    }
    private String authorizationExecute(Map<String, String> params,String url,String account,String passwordOrToken) throws IOException {

        URI uri = URI.create(url);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),new UsernamePasswordCredentials(account, passwordOrToken));
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(host, basicAuth);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        HttpPost httpPost = new HttpPost(uri);

        if (null != params && !params.isEmpty()) {
            List<BasicNameValuePair> pairs = new ArrayList<>();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairs,"UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
        }

        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        HttpResponse response = null;

        try {
            response = httpClient.execute(host, httpPost, localContext);

//            log.info(EntityUtils.toString(response.getEntity()));
            Integer code = response.getStatusLine().getStatusCode();

            if ( code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
                return EntityUtils.toString(response.getEntity(),"utf-8");
            }

            return null;
        } catch (Exception e) {
            httpPost.abort();
            e.printStackTrace();
            return null;
        } finally {

            if (response != null) {
                ((CloseableHttpResponse) response).close();
            }

            httpPost.releaseConnection();
            httpClient.close();
        }

    }

    public static void main(String[] args) throws Exception {

//        String url = "http://localhost:8060/user/qrl758/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken";
//
//
////        HttpUtil.formDataPost(urlString,pairs);
//
//        URI uri = URI.create(url);
//        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),new UsernamePasswordCredentials("qrl758", "12345678"));
//        AuthCache authCache = new BasicAuthCache();
//        BasicScheme basicAuth = new BasicScheme();
//        authCache.put(host, basicAuth);
//        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//        HttpPost httpPost = new HttpPost(uri);
//
//        HttpClientContext localContext = HttpClientContext.create();
//        localContext.setAuthCache(authCache);
//        HttpResponse response = null;
//
//        try {
//            response = httpClient.execute(host, httpPost, localContext);
//            System.out.println(response.getStatusLine().getStatusCode());
//
//            String str = EntityUtils.toString(response.getEntity(),"utf-8");
//
//            System.out.println(JSONObject.parseObject(str).getJSONObject("data").getString("tokenValue"));
//
//
//        } catch (Exception e) {
//            httpPost.abort();
//            e.printStackTrace();
//        } finally {
//
//            if (response != null) {
//                ((CloseableHttpResponse) response).close();
//            }
//
//            httpPost.releaseConnection();
//            httpClient.close();
//        }
//        JenkinsServer server = new JenkinsServer(new URI("http://localhost:8060"),"qrl758","11a74babd53344e010bdd2a60dccea45cf");
//
//        JobWithDetails jobWithDetails = server.getJob("guyue");
        String url = "http://localhost:8080/jenkins/job/guyue/34/";
        URL url1 = new URL(url);

        System.out.println(url1.getPath().replace("jenkins/",""));
//        URI uri = URI.create(urlString);
//        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials("admin", "e20e6ba9448f468fb39dec3529984cfc"));
//        // Create AuthCache instance
//        AuthCache authCache = new BasicAuthCache();
//        // Generate BASIC scheme object and add it to the local auth cache
//        BasicScheme basicAuth = new BasicScheme();
//        authCache.put(host, basicAuth);
//        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//        HttpGet httpGet = new HttpGet(uri);
//        // Add AuthCache to the execution context
//        HttpClientContext localContext = HttpClientContext.create();
//        localContext.setAuthCache(authCache);
//
//        HttpResponse response = httpClient.execute(host, httpGet);

//        System.out.println(EntityUtils.toString(response.getEntity()));
    }
}
