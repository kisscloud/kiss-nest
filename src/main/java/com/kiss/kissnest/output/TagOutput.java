package com.kiss.kissnest.output;

import lombok.Data;

@Data
public class TagOutput {

    private String tagName;

    private String description;

    private String message;

    private Long createdAt;
}
