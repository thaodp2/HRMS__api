package com.minswap.hrms.constants;

import java.util.Arrays;
import java.util.List;

public class CommonConstant {
    public static final String BEARER_TOKEN = "Bearer ";

    private CommonConstant() {
    }
    public static final List<String> ACTUATOR_PATHS = Arrays.asList("/actuator/info", "/actuator/health");

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.[SSS][SS][S]";

    public static final String YYYY_MM_DD_HH_MM= "yyyy-MM-dd HH:mm:ss";
    
    public static final String X_CHANNEL_CODE_HEADER = "X-Channel-Code";
    
    public static final String X_SESSION_ID_HEADER = "X-Session-ID";
    
    public static final String HEALTH_CHECK = "/actuator";
    
    public static final String HRMS_PROCESSING_SERVICE_ID = "HRMS";
    
    public static final String MANAGER = "/manager";
    
    public static final String HR = "/hr";
    
    // vunt
    public static final Integer UPDATE_SUCCESS = 1;

    public static final Integer UPDATE_FAIL = 0;

    public static final String USER = "/user";
}
