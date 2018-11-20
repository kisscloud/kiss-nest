package com.kiss.kissnest.status;

import status.CodeEnums;

public class NestStatusCode extends CodeEnums {


    public static final Integer TEAM_GROUPS_EXIST = 1001;
    public static final Integer CREATE_TEAM_FAILED = 1002;
    public static final Integer TEAM_NOT_EXIST = 1003;
    public static final Integer UPDATE_TEAM_FAILED = 1004;
    public static final Integer CREATE_TEAM_REPOSITORY_FAILED = 1004;
    public static final Integer TEAM_NAME_IS_EMPTY = 1005;
    public static final Integer TEAM_NAME_EXIST = 1006;
    public static final Integer TEAMID_IS_EMPTY = 1007;
    public static final Integer TEAMID_EXIST = 1008;
    public static final Integer TEAM_SLUG_IS_EMPTY = 1009;


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
    public static final Integer GROUP_REPOSITORYID_NOT_EXIST = 4009;
    public static final Integer GROUP_NAME_IS_EMPTY = 4010;
    public static final Integer GROUP_NAME_EXIST = 4011;
    public static final Integer GROUP_ID_IS_EMPTY = 4012;
    public static final Integer GROUP_ID_NOT_EXIST = 4013;
    public static final Integer GROUP_STATUS_IS_EMPTY = 4014;
    public static final Integer GROUP_SLUG_IS_EMPTY = 4015;
    public static final Integer GROUP_SLUG_IS_EXIST = 4016;
    public static final Integer DELETE_GROUP_REPOSITORY_FAILED = 4017;

    public static final Integer PROJECT_NAME_EXIST = 5001;
    public static final Integer CREATE_PROJECT_FAILED = 5002;
    public static final Integer PROJECT_NOT_EXIST = 5003;
    public static final Integer DELETE_PROJECT_FAILED = 5004;
    public static final Integer UPDATE_PROJECT_FAILED = 5005;
    public static final Integer PROJECT_SLUG_EMPTY = 5006;
    public static final Integer CREATE_PROJECT_REPOSITORY_FAILED = 5007;
    public static final Integer PROJECT_ID_IS_EMPTY = 5008;
    public static final Integer PROJECT_REPOSITORY_EXIST = 5009;
    public static final Integer PROJECT_ID_NOT_EXIST = 5010;
    public static final Integer PROJECT_NAME_IS_EMPTY = 5011;
    public static final Integer PROJECT_TYPE_IS_EMPTY = 5012;
    public static final Integer PROJECT_SLUG_IS_EXIST = 5013;


    public static final Integer MEMBER_NAME_EXIST = 6001;
    public static final Integer CREATE_MEMBER_FAILED = 6002;
    public static final Integer MEMBER_NOT_EXIST = 6003;
    public static final Integer DELETE_MEMBER_FAILED = 6004;
    public static final Integer UPDATE_MEMBER_FAILED = 6005;
    public static final Integer MEMBER_DEFAULT_TEAM_DEFECT = 6006;
    public static final Integer MEMBER_PASSWORD_ERROR = 6007;
    public static final Integer MEMBER_CLIENTID_IS_EMPTY = 6008;
    public static final Integer MEMBER_NAME_IS_EMPTY = 6009;
    public static final Integer MEMBER_ACCOUNT_ID_IS_EMPTY = 6010;
    public static final Integer MEMBER_LIST_IS_EMPTY = 6011;
    public static final Integer CREATE_MEMBER_GROUP_FAILED = 6012;
    public static final Integer GROUP_MEMBER_IS_EXIST = 6013;
    public static final Integer TEAM_MEMBER_IS_EXIST = 6014;
    public static final Integer CREATE_MEMBER_PROJECT_FAILED = 6015;
    public static final Integer PROJCET_MEMBER_IS_EXIST = 6016;


    public static final Integer CREATE_GROUP_PROJECT_FAILED = 7001;

    public static final Integer BIND_ACCOUNT_TEAM_FAILED = 8001;
    public static final Integer CREATE_MEMBER_TEAM_FAILED = 8002;


    public static final Integer CREATE_MEMBER_ACCESS_FAILED = 9001;
    public static final Integer UPDATE_MEMBER_ACCESS_FAILED = 9002;
    public static final Integer CREATE_MEMBER_APITOKEN_FAILED = 9003;
    public static final Integer UPDATE_MEMBER_APITOKEN_FAILED = 9004;
    public static final Integer MEMBER_APITOKEN_IS_EMPTY = 9005;

    public static final Integer CREATE_JENKINS_JOB_ERROR = 10001;
    public static final Integer BUILD_JENKINS_JOB_ERROR = 10002;
    public static final Integer SHELL_IS_EMPTY = 10003;
    public static final Integer TYPE_IS_EMPTY = 10004;
    public static final Integer BRANCH_IS_EMPTY = 10005;
    public static final Integer DEPLOY_JENKINS_JOB_ERROR = 10006;
    public static final Integer UPDATE_JOB_FAILED= 10007;
    public static final Integer UPDATE_JENKINS_JOB_ERROR = 10008;
    public static final Integer JOB_ID_IS_EMPTY = 10009;
    public static final Integer JOB_NOT_EXIST = 10010;
    public static final Integer DELETE_JOB_FAILED = 10011;
    public static final Integer CREATE_BUILD_LOG_FAILED = 10012;
    public static final Integer EXECJOB_TYPE_IS_EMPTY = 10013;
    public static final Integer EXECJOB_TYPE_ERROR = 10014;




    public static final Integer PAGE_IS_EMPTY = 11001;
    public static final Integer PAGE_IS_ERROR = 11002;
    public static final Integer SIZE_IS_EMPTY = 11003;
    public static final Integer SIZE_IS_ERROR = 11004;

    public static final Integer SERVER_ENVIRONMENT_CREATE_FAILED = 12001;
    public static final Integer SERVER_CREATE_FAILED = 12002;
    public static final Integer SERVER_ENVIRONMENT_NAME_IS_EMPTY = 12003;
    public static final Integer SERVER_ENVIRONMENT_TYPE_IS_EMPTY = 12004;
    public static final Integer SERVER_SERVER_NAME_IS_EMPTY = 12005;
    public static final Integer SERVER_SERVER_NAME_EXIST = 12006;
    public static final Integer SERVER_ENVIRONMENT_NAME_IS_EXIST = 12007;
    public static final Integer UPDATE_SERVER_ENVIRONMENT_FAILED = 12008;
    public static final Integer SERVER_ENVIRONMENT_HAS_SERVERS = 12009;
    public static final Integer DELETE_SERVER_ENVIRONMENT_FAILED = 12010;
    public static final Integer SERVER_ENVIRONMENT_NOT_EXIST = 12011;


    public static final Integer SERVER_ENVID_IS_EMPTY = 13001;
    public static final Integer SERVER_ENVID_NOT_EXIST = 13002;
    public static final Integer SERVER_INNERIP_IS_EMPTY = 13003;
    public static final Integer SERVERID_IS_EMPTY = 13004;
    public static final Integer SERVER_NOT_EXIST = 13005;
    public static final Integer SERVER_UPDATE_FAILED = 13006;
    public static final Integer SERVER_HAS_JOB = 13007;
    public static final Integer DELETE_SERVER_FAILED = 13008;



    public static final Integer TEAM_ID_IS_EMPTY = 14001;
    public static final Integer LINK_TITLE_IS_EMPTY = 14002;
    public static final Integer LINK_URL_IS_EMPTY = 14003;


    public static final Integer SERVICE_ERROR = 3001;

}
