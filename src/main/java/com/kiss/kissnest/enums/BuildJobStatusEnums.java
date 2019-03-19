package com.kiss.kissnest.enums;

public enum BuildJobStatusEnums {

    FAILED(0, "失败"),
    SUCCESS(1, "成功"),
    PENDING(2, "初始化"),
    QUEUEING(3, "排队中"),
    BUILDING(4, "构建中"),
    PACKAGE_MISS(5, "部署包丢失");

    private Integer value;

    BuildJobStatusEnums(Integer value, String description) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
