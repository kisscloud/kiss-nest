package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class JenkinsNotificationSCM {
    private String url;
    private String branch;
    private String commit;
}
