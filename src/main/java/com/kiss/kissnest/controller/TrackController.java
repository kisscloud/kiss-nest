package com.kiss.kissnest.controller;

import com.kiss.kissnest.output.TrackOutput;
import com.kiss.kissnest.service.TrackService;
import com.kiss.kissnest.util.JenkinsUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@Api(tags = "WebHook", description = "动态记录")
@Slf4j
public class TrackController {

    @Autowired
    private JenkinsUtil jenkinsUtil;

    @Autowired
    private TrackService trackService;

    @PostMapping("/track")
    @ApiOperation(value = "接收动态信息")
    public String note(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        String aux = "";

        while ((aux = request.getReader().readLine()) != null) {
            builder.append(aux);
        }

        String text = builder.toString();
        log.info(text);
        trackService.createTrack(text);
        return "success";
    }

    @GetMapping("/track")
    public List<TrackOutput> getTrack(@RequestParam("teamId") Integer teamId) {

        return trackService.getTracksByTeamId(teamId);
    }
}
