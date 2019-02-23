package com.kiss.kissnest.enums;

public enum WebSocketMessageTypeEnums {

    BUILD_PROJECT_START("buildProjectStart"),
    BUILD_PROJECT_END("buildProjectEnd"),
    DEPLOY_PROJECT_START("deployProjectStart"),
    DEPLOY_PROJECT_END("deployProjectEnd"),
    SERVER_MONITOR_LOG("serverMonitorLog"),
    PROGRAM_MONITOR_LOG("programMonitorLog");

    private String value;

    WebSocketMessageTypeEnums(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
