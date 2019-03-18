package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class JenkinsNotificationBuild {

    private String full_url;
    private Integer number;
    private Integer queue_id;
    private String phase;
    private String status;
    private String url;
    private JenkinsNotificationSCM scm;
    private String notes;
    private String log;
}
