package com.minswap.hrms.constants;

import java.util.*;

public class CommonConstant {
    public static final String BEARER_TOKEN = "Bearer ";
    public static final long MILLISECOND_7_HOURS = 7 * 60 * 60 * 1000;
    public static final long MILLISECOND_2_HOURS = 2 * 60 * 60 * 1000;
    private CommonConstant() {
    }

    public static final List<String> ACTUATOR_PATHS = Arrays.asList("/actuator/info", "/actuator/health");
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.[SSS][SS][S]";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";
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

    public static final Long REQUEST_TYPE_ID_OF_BORROW_DEVICE = Long.valueOf(11);

    public static String[] LIST_HEADER_EMPLOYEE = {
            "Roll Number",
            "Employee Name",
            "Email",
            "Department",
            "Position",
            "Status"
    };

    public static String[] LIST_HEADER_BENEFIT_BUDGET = {
            "Roll Number",
            "Employee Name",
            "Budget",
            "Used",
            "Remain Of Month",
            "Remain Of Year"
    };

    public static String[] LIST_HEADER_TIME_CHECK = {
            "Roll Number",
            "Employee Name",
            "Mon",
            "Tus",
            "Wed",
            "Thu",
            "Fri",
            "Sat",
            "Sun"
    };

    public static String[] TEMPLATE_HEADER_TO_IMPORT = {
            "Roll Number",
            "Full Name",
            "Date Of Birth",
            "Manager Roll Number",
            "Department Id",
            "Position",
            "Position Id",
            "Rank",
            "Rank Id",
            "Citizen Identification",
            "Phone Number",
            "Address",
            "Gender",
            "Gender Code",
            "Salary Basic",
            "Salary Bonus",
            "Is Manager",
            "Is Manager Code",
            "Is Active",
            "Is Active Code"
    };

    public static String[] TEMPLATE_HEADER_TIME_CHECK_TO_IMPORT = {
            "Private Key",
            "Time Log"
    };

    public static Long[] LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET = {
            Long.valueOf(1),
            Long.valueOf(6),
            Long.valueOf(3),
            Long.valueOf(8),
            Long.valueOf(10)
    };
    public static final Long ROLE_ID_OF_MANAGER = Long.valueOf(2);
    public static final Long ROLE_ID_OF_IT_SUPPORT = Long.valueOf(5);
    public static final Long RANK_ID_OF_INTERN = Long.valueOf(1);
    public static final String URL = "https://api.ms-hrms.software/human-resources-management-system/";
}
