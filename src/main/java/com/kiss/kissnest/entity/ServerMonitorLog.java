package com.kiss.kissnest.entity;

import lombok.Data;

@Data
public class ServerMonitorLog {
    private Integer id;
    private Integer teamId;
    private Integer envId;
    private Integer serverId;
    private String serverInfo;
}
