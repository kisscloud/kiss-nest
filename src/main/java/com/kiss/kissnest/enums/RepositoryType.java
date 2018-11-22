package com.kiss.kissnest.enums;

public enum RepositoryType {

    Group(10),
    SubGroup(20),
    Project(300);

    public final int repositoryType;

    RepositoryType(int repositoryType) {
        this.repositoryType = repositoryType;
    }
}
