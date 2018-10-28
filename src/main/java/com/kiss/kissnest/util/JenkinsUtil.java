package com.kiss.kissnest.util;

import com.offbytwo.jenkins.JenkinsServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JenkinsUtil {

    @Value("${jenkins.account}")
    private String account;

    @Value("${jenkins.password}")
    private String password;

    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.buildWithParameterUrl}")
    private String buildWithParameterUrl;

    @Value("${jenkins.buildUrl}")
    private String buildUrl;

    public boolean createJob (String jobName,String configPath) {

        JenkinsServer server = null;

        try {
            server = new JenkinsServer(new URI(jenkinsUrl),account,password);
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

    public boolean buildJob (String jobName,String branch) {

        try {
            Map<String,String> params = new HashMap<>();
            String url = String.format(buildUrl,jobName);
            if (branch != null) {
                params.put("branch","master");
                url = String.format(buildWithParameterUrl,jobName);
            }

            return buildJob(jobName,params,url);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    private boolean buildJob(String jobName,Map<String, String> params,String url) throws IOException {

        URI uri = URI.create(url);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),new UsernamePasswordCredentials(account, password));
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

            log.info(EntityUtils.toString(response.getEntity()));

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }

            return false;
        } catch (Exception e) {
            httpPost.abort();
            e.printStackTrace();
            return false;
        } finally {

            if (response != null) {
                ((CloseableHttpResponse) response).close();
            }

            httpPost.releaseConnection();
            httpClient.close();
        }

    }
}
