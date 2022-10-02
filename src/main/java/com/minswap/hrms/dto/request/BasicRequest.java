package com.minswap.hrms.dto.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("request_id")
	@NotNull(message = "500001")
	private String requestId;

	@JsonProperty("device_id")
	private String deviceId;

	@JsonProperty("device_os")
	private String deviceOs;

	@JsonProperty("device_session_id")
	private String deviceSessionId;

	@JsonProperty("device_version")
	private String deviceVersion;

	/** The lang. */
	@JsonProperty("lang")
	private String lang;

	@JsonProperty("device_longitude")
	private String deviceLongitude;

	@JsonProperty("device_latitude")
	private String deviceLatitude;
	
	@JsonProperty("app_code")
    private String appCode;
	
}
