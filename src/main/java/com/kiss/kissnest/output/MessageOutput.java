package com.kiss.kissnest.output;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageOutput implements Serializable {

    private String bo;

    private Object data;

    public MessageOutput(String bo, Object data) {
        this.bo = bo;
        this.data = data;
    }
}
