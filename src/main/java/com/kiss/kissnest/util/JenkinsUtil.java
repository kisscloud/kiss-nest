package com.kiss.kissnest.util;

import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.entity.CrumbEntity;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.kiss.foundation.utils.ThreadLocalUtil;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JenkinsUtil {

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.buildWithParameterUrl}")
    private String buildWithParameterUrl;

    @Value("${jenkins.buildUrl}")
    private String buildUrl;

    @Value("${jenkins.generateTokenUrl}")
    private String generateTokenUrl;

    @Value("${jenkins.bin.ip}")
    private String jenkinBinIp;

    @Value("${jenkins.crumbPath}")
    private String jenkinsCrumbPath;

    @Value("${jenkins.queuePath}")
    private String jenkinsQueuePath;

    public boolean createJob(String jobName, String configPath, String account, String passwordOrToken) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl), account, passwordOrToken);
            StringBuilder builder = readFile(configPath);
            if (builder == null) {
                return false;
            }

            server.createJob(jobName, builder.toString(), false);

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

    public boolean createJobByShell(String jobName, String path, String shell, String sshUrl, String account, String passwordOrToken) {
        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl), account, passwordOrToken);
            StringBuilder builder = readFileFromClassPath("/config.xml");
            String formatShell = StringEscapeUtils.escapeHtml(shell);
            String script = String.format(builder.toString(), sshUrl, jobName, path, jenkinBinIp, formatShell);

            if (builder == null) {
                return false;
            }

            server.createJob(jobName, script, false);

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

    public boolean updateJob(String jobName, String path, String shell, String sshUrl, String account, String passwordOrToken) {
        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl), account, passwordOrToken);
            StringBuilder builder = readFileFromClassPath("/config.xml");
            String formatShell = StringEscapeUtils.escapeHtml(shell);
            String script = String.format(builder.toString(), sshUrl, jobName, path, jenkinBinIp, formatShell);

            if (builder == null) {
                return false;
            }

            server.updateJob(jobName, script, false);

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

    public boolean deleteJob(String jobName, String account, String passwordOrToken) {
        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl), account, passwordOrToken);
            server.deleteJob(jobName);

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


    public String buildJob(String jobName, String branch, String account, String passwordOrToken) {

        try {
            Map<String, String> params = new HashMap<>();
            String url = String.format(buildUrl, jobName);
            if (branch != null) {
                params.put("branch", "master");
                url = String.format(buildWithParameterUrl, jobName);
            }

            HttpResponse auth = authorizationExecute(params, url, account, passwordOrToken);
            Header[] headers = auth.getHeaders("Location");

            String location = null;
            if (headers != null && headers.length != 0) {
                location = headers[0] == null ? null : headers[0].getValue();
            }

            return location;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateApiToken(String account, String password) {

        String url = String.format(generateTokenUrl, account);

        try {
            authorizationExecute(null, url, account, password);
            String result = ThreadLocalUtil.getString("entity");
            return JSONObject.parseObject(result).getJSONObject("data").getString("tokenValue");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public StringBuilder readFile(String configPath) {
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

    public BuildWithDetails getLastBuildWithDetail(String jobName, String account, String passwordOrToken) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl), account, passwordOrToken);

            FolderJob folderJob = new FolderJob(jobName, jenkinsUrl);
//            folderJob.createFolder("http://localhost:8060");
            JobWithDetails jobWithDetails = server.getJob(folderJob, jobName);

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

    public JenkinsServer getJenkinsServer(String account, String passwordOrToken) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl), account, passwordOrToken);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return server;
        }
    }

    public Build getBuild(String jobName, JenkinsServer server, Integer number) {

        try {
            JobWithDetails jobWithDetails = server.getJob(jobName);
            Build build = jobWithDetails.getBuildByNumber(number);

            return build;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Build getBuild(JenkinsServer server, String location) {

        try {
            QueueReference queueReference = new QueueReference(location + jenkinsQueuePath);
            QueueItem queueItem = server.getQueueItem(queueReference);

            if (queueItem == null) {
                return null;
            }

            Build build = server.getBuild(queueItem);

            return build;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public Build getLastBuild(String jobName, JenkinsServer server) {

        try {
            JobWithDetails jobWithDetails = server.getJob(jobName);
            Build build = jobWithDetails.getLastBuild();

            return build;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BuildWithDetails getLastBuildWithDetail(JenkinsHttpConnection client, String url) {

        try {
            BuildWithDetails buildWithDetails = client.get(url, BuildWithDetails.class);
            return buildWithDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getConsoleOutputText(JenkinsHttpConnection client, String url) {

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

    public StringBuilder readFileFromClassPath(String name) throws IOException {

        InputStream in = JenkinsUtil.class.getResourceAsStream(name);
        StringBuilder builder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {
            builder.append(lineTxt);
        }

        return builder;
    }

    public HttpResponse authorizationExecute(Map<String, String> params, String url, String account, String passwordOrToken) throws IOException {

        URI uri = URI.create(url);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials(account, passwordOrToken));
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

            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
            httpPost.setEntity(urlEncodedFormEntity);
        }

        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);
        HttpResponse response = null;
        CrumbEntity crumbEntity = getCrumb(jenkinsUrl, jenkinsCrumbPath, account, passwordOrToken);
        try {
            httpPost.addHeader(crumbEntity.getCrumbRequestField(), crumbEntity.getCrumb());
            response = httpClient.execute(host, httpPost, localContext);

//            log.info(EntityUtils.toString(response.getEntity()));
            Integer code = response.getStatusLine().getStatusCode();

            if (code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                ThreadLocalUtil.setString("entity", result);
                return response;
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

    public CrumbEntity getCrumb(String uri, String path, String username, String password) {
        JenkinsHttpClient jenkinsHttpClient = null;
        try {
            jenkinsHttpClient = new JenkinsHttpClient(new URI(uri), username, password);
            String jsonResult = jenkinsHttpClient.get(path);
            CrumbEntity crumbEntity = JsonUtil.getJsonObject(jsonResult, CrumbEntity.class);

            return crumbEntity;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jenkinsHttpClient.close();
        }
        return null;
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
//        JenkinsServer server = new JenkinsServer(new URI("http://localhost:8060"),"xiaoyue","11eeeddf7afeb80418ae6cd2e9576d9786");
//
//        InputStream in = JenkinsUtil.class.getResourceAsStream("/config.xml");
//        StringBuilder builder = new StringBuilder();
//        InputStreamReader reader = new InputStreamReader(in);
//        BufferedReader bufferedReader = new BufferedReader(reader);
//        String lineTxt = null;
//        while ((lineTxt = bufferedReader.readLine()) != null) {
//            builder.append(lineTxt);
//        }
//
////        String script = String.format(builder.toString(),"aaa");
//
//        server.createJob("test11",builder.toString(),false);
//        System.out.println();
//        authorizationExecute(null,"http://build.kisscloud.io/user/xiaohu/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken","xiaohu","12345678");
//        JobWithDetails jobWithDetails = server.getJob("guyue");
//        Build build = jobWithDetails.getLastBuild();
//        JenkinsHttpConnection client = build.getClient();
//        BuildWithDetails buildWithDetails = client.get("http://localhost:8060/job/guyue/1000",BuildWithDetails.class);
//        System.out.println();
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

//        JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI("http://build.kisscloud.io"),"xiaowang","12345678");
////        String str = jenkinsHttpClient.get("/crumbIssuer/api/json");
////
////        System.out.println(str);
//        JenkinsServer server = null;
//
//        try {
//            server = new JenkinsServer(new URI("http://build.kisscloud.io"),"xiaohu","1156f50033ef17fb469f7d945da1e246d4");
//            InputStream in = JenkinsUtil.class.getResourceAsStream("/config.xml");
//            StringBuilder builder = new StringBuilder();
//            InputStreamReader reader = new InputStreamReader(in);
//            BufferedReader bufferedReader = new BufferedReader(reader);
//            String lineTxt = null;
//            while ((lineTxt = bufferedReader.readLine()) != null) {
//                builder.append(lineTxt);
//            }
//            String formatShell = StringEscapeUtils.escapeHtml("abc");
//            String script = String.format(builder.toString(),"git@git.kisscloud.io:facebit/facebitgroup/kiss-eureka-server.git","face5","192.168.0.192",formatShell);
//
//            server.createJob("face5",script,false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (server != null) {
//                server.close();
//            }
//        }

        try {
            Map<String, String> params = new HashMap<>();
//            String url = String.format("http://build.kisscloud.io/job/%s/buildWithParameters", "kiss-eureka-server");
            String url = "http://build.kisscloud.io/user/xiaoqian/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken";
            URI uri = URI.create(url);
            HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()), new UsernamePasswordCredentials("xiaoqian", "12345678"));
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

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(pairs, "UTF-8");
                httpPost.setEntity(urlEncodedFormEntity);
            }

            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);
            HttpResponse response = null;

            JenkinsHttpClient jenkinsHttpClient = new JenkinsHttpClient(new URI("http://build.kisscloud.io"), "xiaoqian", "12345678");
            String jsonResult = jenkinsHttpClient.get("/crumbIssuer/api/json");
            CrumbEntity crumbEntity = JsonUtil.getJsonObject(jsonResult, CrumbEntity.class);
            try {
                httpPost.addHeader(crumbEntity.getCrumbRequestField(), crumbEntity.getCrumb());
                response = httpClient.execute(host, httpPost, localContext);

//            log.info(EntityUtils.toString(response.getEntity()));
                Integer code = response.getStatusLine().getStatusCode();

                if (code == HttpStatus.SC_OK || code == HttpStatus.SC_CREATED) {
                    HttpEntity httpEntity = response.getEntity();
//                    InputStream in = httpEntity.getContent();
//                    Header[] heads = response.getHeaders("Location");
//                    String value = heads[0].getValue();
                    System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
                    System.out.println(httpEntity);
                }
                System.out.println("aaa");
            } catch (Exception e) {
                httpPost.abort();
                e.printStackTrace();
            } finally {

                if (response != null) {
                    ((CloseableHttpResponse) response).close();
                }

                httpPost.releaseConnection();
                httpClient.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        JenkinsServer server = null;
//
//        try {
//            server = new JenkinsServer(new URI("http://build.kisscloud.io"), "xiaohu", "12345678");
//            QueueReference queueReference = new QueueReference("http://build.kisscloud.io/queue/item/57/api/json");
//            QueueItem queueItem = server.getQueueItem(queueReference);
//
//            Build build = server.getBuild(queueItem);
//            System.out.println(build);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }


    }
}
