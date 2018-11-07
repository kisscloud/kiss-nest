package com.kiss.kissnest.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.kiss.kissnest.util.JenkinsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/kiss/nest")
@Api(tags = "WebHook", description = "动态记录")
public class WebHook {

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @GetMapping("/note")
    @ApiOperation(value = "接收动态信息")
    public String note (HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        String aux = "";
        JSONObject json = null;
        JSONArray array=null;
        JSONObject arraytojson=null;
        while ((aux = request.getReader().readLine()) != null) {
            builder.append(aux);
        }
        String text = builder.toString();
        try {
            json = JSONObject.parseObject(text);
            array=(JSONArray) json.get("commits");
            arraytojson=(JSONObject) array.get(0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(arraytojson);
        return "success";
    }

    @GetMapping("/test")
    public String test(){

        Map<String,String> parameters = new HashMap<>();
        parameters.put("branch","master");
        boolean str = jenkinsUtil.buildJob("guyue","master","qrl758","11a74babd53344e010bdd2a60dccea45cf");

        jenkinsUtil.getLastBuildWithDetail("guyue","qrl758","11a74babd53344e010bdd2a60dccea45cf");
        return "hello";
    }
}
