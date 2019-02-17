package com.kiss.kissnest.enums;

public enum BuildJobStatusEnums {

    FAILED(0, "失败"),
    SUCCESS(1, "成功"),
    PENDING(2, "执行中");

    private Integer value;

    BuildJobStatusEnums(Integer value, String description) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
