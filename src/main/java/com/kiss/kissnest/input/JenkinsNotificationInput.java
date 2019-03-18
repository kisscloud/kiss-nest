package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class JenkinsNotificationInput {

    private String name;
    private String url;
    private Build build;

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

@Data
class Build {
    private String full_url;
    private Integer number;
    private Integer queueId;
    private String phase;
    private String status;
    private String url;
    private SCM scm;
}

@Data
class SCM {
    private String url;
    private String branch;
    private String commit;
}









