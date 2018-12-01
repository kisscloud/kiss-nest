package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateTagInput {

    private Integer projectId;

    private String tagName;

    private String ref;

    private String message;

    private String releaseDescription;
}
