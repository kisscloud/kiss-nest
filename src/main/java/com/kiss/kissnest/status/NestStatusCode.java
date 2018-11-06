package com.kiss.kissnest.status;

import status.CodeEnums;

public class NestStatusCode extends CodeEnums {


    public static final Integer TEAM_GROUPS_EXIST = 1001;
    public static final Integer CREATE_TEAM_FAILED = 1002;
    public static final Integer TEAM_NOT_EXIST = 1003;
    public static final Integer UPDATE_TEAM_FAILED = 1004;
    public static final Integer CREATE_TEAM_REPOSITORY_FAILED = 1004;

    public static final Integer CREATE_TEAMGROUP_FAILED = 2002;
    public static final Integer GROUP_NOT_EXIST = 2003;

    public static final Integer GROUP_EXIST = 4001;
    public static final Integer CREATE_GROUP_FAILED = 4002;
    public static final Integer GROUP_PROJECT_EXIST = 4003;
    public static final Integer DELETE_GROUP_FAILED = 4004;
    public static final Integer UPDATE_GROUP_FAILED = 4005;
    public static final Integer GROUP_PARENTID_LOSED = 4006;
    public static final Integer CREATE_GROUP_REPOSITORY_FAILED = 4007;
    public static final Integer PROJECT_MASTER_GROUP_NOT_EXIST = 4008;
    public static final Integer GROUP_REPOSITORYID_NOT_EXIST = 4008;




    public static final Integer PROJECT_NAME_EXIST = 5001;
    public static final Integer CREATE_PROJECT_FAILED = 5002;
    public static final Integer PROJECT_NOT_EXIST = 5003;
    public static final Integer DELETE_PROJECT_FAILED = 5004;
    public static final Integer UPDATE_PROJECT_FAILED = 5005;
    public static final Integer PROJECT_SLUG_EMPTY= 5005;
    public static final Integer CREATE_PROJECT_REPOSITORY_FAILED = 5006;



    public static final Integer MEMBER_NAME_EXIST = 6001;
    public static final Integer CREATE_MEMBER_FAILED = 6002;
    public static final Integer MEMBER_NOT_EXIST = 6003;
    public static final Integer DELETE_MEMBER_FAILED = 6004;
    public static final Integer UPDATE_MEMBER_FAILED = 6005;

    public static final Integer CREATE_GROUP_PROJECT_FAILED = 7001;

    public static final Integer BIND_ACCOUNT_TEAM_FAILED = 8001;

    public static final Integer CREATE_MEMBER_ACCESS_FAILED = 9001;
    public static final Integer UPDATE_MEMBER_ACCESS_FAILED = 9002;
    public static final Integer CREATE_MEMBER_APITOKEN_FAILED = 9003;
    public static final Integer UPDATE_MEMBER_APITOKEN_FAILED = 9004;
    public static final Integer MEMBER_APITOKEN_IS_EMPTY = 9005;

    public static final Integer CREATE_JENKINS_JOB_ERROR = 10001;
    public static final Integer BUILD_JENKINS_JOB_ERROR = 10002;

    public static final Integer SERVICE_ERROR = 3001;

}
