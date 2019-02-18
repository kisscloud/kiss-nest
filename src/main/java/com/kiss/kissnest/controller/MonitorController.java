package com.kiss.kissnest.controller;

import com.kiss.kissnest.input.MonitorProgramInput;
import com.kiss.kissnest.input.MonitorServerInput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Monitor", description = "监控相关接口")
@Slf4j
public class MonitorController {

    @PostMapping("/monitor/server")
    @ApiOperation(value = "服务器监控")
    public void serverMonitor(@RequestBody MonitorServerInput monitorServerInput) {
        log.info("{}", monitorServerInput);
    }

    @PostMapping("/monitor/program")
    @ApiOperation(value = "程序监控")
    public void programMonitor(@RequestBody MonitorProgramInput monitorProgramInput) {
        log.info("{}", monitorProgramInput);
    }
}
