package com.kiss.kissnest.enums;

public enum BuildJobTypeEnums {

    BRANCH(1, "分支"),
    TAG(2, "版本");

    private Integer value;

    BuildJobTypeEnums(Integer value, String description) {
        this.value = value;
    }

    public Integer value() {
        return value;
    }
}
