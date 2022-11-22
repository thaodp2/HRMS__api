package com.minswap.hrms.constants;

import java.util.*;

public class CommonConstant {
    public static final String BEARER_TOKEN = "Bearer ";
    private CommonConstant() {
    }

    public static final List<String> ACTUATOR_PATHS = Arrays.asList("/actuator/info", "/actuator/health");
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.[SSS][SS][S]";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm:ss";
    public static final String X_CHANNEL_CODE_HEADER = "X-Channel-Code";
    public static final String X_SESSION_ID_HEADER = "X-Session-ID";
    public static final String HEALTH_CHECK = "/actuator";
    public static final String HRMS_PROCESSING_SERVICE_ID = "HRMS";
    public static final String MANAGER = "/manager";
    public static final String HR = "/hr";
    public static final String ITSUPPORT = "/it-support";
    public static final Integer UPDATE_SUCCESS = 1;

    public static final Integer UPDATE_FAIL = 0;
    public static final String ALL = "All";
    public static final String SUBORDINATE = "Subordinate";
    public static final String MY = "My";
    public static final String CREATE_DATE_FIELD = "createDate";
    public static final String START_TIME_FIELD = "startTime";
    public static final String END_TIME_FIELD = "endTime";
    public static final String ROLL_NUMBER_FIELD = "rollNumber";

    public static final String FULL_NAME_FIELD = "fullName";

    public static final String USED_FIELD = "used";

    public static final Long REQUEST_TYPE_ID_OF_ANNUAL_LEAVE = Long.valueOf(1);

    public static final Long REQUEST_TYPE_ID_OF_OT = Long.valueOf(7);

    public static String[] LIST_HEADER_EMPLOYEE = {
            "Roll Number",
            "Employee Name",
            "Email",
            "Department",
            "Position",
            "Status"
    };

    public static String[] LIST_HEADER_LEAVE_BUDGET = {
            "Employee Name",
            "Leave Budget",
            "Number of day off",
            "Remain day off"
    };

    public static Long[] LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET = {
            Long.valueOf(1),
            Long.valueOf(6),
            Long.valueOf(3),
            Long.valueOf(8),
            Long.valueOf(10)
    };
    public static final Long ROLE_ID_OF_MANAGER = Long.valueOf(2);
}
