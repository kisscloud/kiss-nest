package com.kiss.kissnest.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SaltStackUtil {

    public String getToken(String url, String username, String password) {
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("eauth", "pam");
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(params));
        Request request = new Request.Builder()
                .url(url + "/login")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String tokenResp = response.body().string();
            JSONObject jsonObject = JSONObject.parseObject(tokenResp);
            JSONArray jsonArray = jsonObject.getJSONArray("return");
            JSONObject tokenJson = jsonArray.getJSONObject(0);
            String token = tokenJson.getString("token");
            return StringUtils.isEmpty(token) ? null : token;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public String callLocalSync(String url, String username, String password, String version, String functionName, String nodes, String option) {
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> params = new HashMap<>();
        params.put("client", "local");
        params.put("tgt", nodes);
        params.put("fun", functionName);
        params.put("arg", option);
        params.put("expr_form", "list");
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(params));
        String token = getToken(url, username, password);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Accept", "application/json")
                .addHeader("X-Auth-Token", token)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            int code = response.code();

            if (code == HttpStatus.SC_OK) {
                ResponseBody responseBody = response.body();
                return responseBody.string();
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
