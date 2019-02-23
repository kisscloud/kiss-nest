package com.kiss.kissnest.output;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageOutput implements Serializable {

    private String type;

    private Object data;

    public MessageOutput(String type, Object data) {
        this.type = type;
        this.data = data;
    }
}
