package com.minswap.hrms.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.checkerframework.checker.regex.qual.Regex;

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

	@JsonProperty("gmail")
	@NotNull(message = "404800")
	private String gmail;
	
}
