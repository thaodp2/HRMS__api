package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeCheckInRequest extends BasicRequest{
	@JsonProperty("id_signature")
	@NotNull(message ="614")
	@NotEmpty(message = "614")
	private String idSignature;

	@JsonProperty("time_log")
	@NotNull(message ="614")
	@NotEmpty(message = "614")
	private String timeLog;
}
