package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class JenkinsNotificationInput {
    private String name;
    private String url;
    private Build build;
}

@Data
class Build {
    private String full_url;
    private Integer number;
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









