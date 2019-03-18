package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class JenkinsNotification {

    private String name;
    private String url;
    private JenkinsNotificationBuild build;


    public boolean isQueued() {
        return build.getPhase().equals("QUEUED");
    }

    public boolean isStarted() {
        return build.getPhase().equals("STARTED");
    }

    public boolean isCompleted() {
        return build.getPhase().equals("COMPLETED");
    }

    public boolean isFinalized() {
        return build.getPhase().equals("FINALIZED");
    }
}













