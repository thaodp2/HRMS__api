package com.minswap.hrms.util;

import static net.logstash.logback.argument.StructuredArguments.entries;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(LogUtil.class);
	
	public static void printError(String requestId, Exception ex) {
		String stacktrace = ExceptionUtils.getStackTrace(ex);
    	Map<String, String> mapCustomizeLog = new HashMap<String, String>();
    	mapCustomizeLog.put("request_id", requestId);
    	mapCustomizeLog.put("exception", stacktrace);
    	logger.error(ex.getLocalizedMessage(), entries(mapCustomizeLog));
	}
}
