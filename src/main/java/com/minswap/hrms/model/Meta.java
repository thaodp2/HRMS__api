package com.minswap.hrms.model;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.RequestHeaderConstant;
import com.minswap.hrms.util.CommonUtil;
import com.minswap.hrms.util.UUIDUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class Meta.
 * @param <R>
 */
@Getter
@Setter
@Slf4j
public class Meta<R> {


	/** The code. */
	@JsonProperty("code")
	private int code;

	/** The message. */
	@JsonProperty("message")
	private String message;

	/** The service code. */
	@JsonProperty("service_id")
	private String serviceId;
	
	@JsonProperty("pagination")
	R extraMeta;

	public static <R> Meta<R> buildMeta(BusinessCode businessCode, R extraMeta) {
        Meta<R> meta = new Meta<>();
        meta.setCode(businessCode.getCode());
        meta.setMessage(businessCode.getMessage());
        meta.setServiceId(CommonConstant.HRMS_PROCESSING_SERVICE_ID);
        meta.setExtraMeta(extraMeta);
        return meta;
    }

}
