package com.kiss.kissnest.enums;

public enum JobTypeEnums {

    BUILD(1, "构建"),
    DEPLOY(2, "部署");

    private Integer value;

    JobTypeEnums(Integer value, String description) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
