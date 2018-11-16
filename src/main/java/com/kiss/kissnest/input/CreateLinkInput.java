package com.kiss.kissnest.input;

import lombok.Data;

@Data
public class CreateLinkInput {

    private Integer teamId;

    private String title;

    private String url;
}
