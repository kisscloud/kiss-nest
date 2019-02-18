package com.kiss.kissnest.input;

import lombok.Data;

import java.math.BigInteger;

@Data
public class MonitorServerInput {
    private String envPath;
    private String innerIp;
    private String hostname;
    private BigInteger bootTime;
    private String os;
    private BigInteger memTotal;
    private BigInteger memFree;
    private Float memUsage;
    private String cpuInfo;
    private Float cpuUsed;
    private BigInteger hdTotal;
    private BigInteger hdFree;
    private Float hdUsage;
    private BigInteger networkReceive;
    private BigInteger networkSent;
}
