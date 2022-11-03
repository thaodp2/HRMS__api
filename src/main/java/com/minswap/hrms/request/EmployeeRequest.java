package com.minswap.hrms.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {
	@JsonProperty("fullName")
	@NotNull(message ="600")
	@NotEmpty(message = "600")
	private String fullName;
	
	@JsonProperty("dateOfBirth")
	@NotNull(message ="601")
	@NotEmpty(message = "601")
	private String dateOfBirth;
	
	@JsonProperty("managerId")
	@NotNull(message ="603")
	@NotEmpty(message = "603")
	private String managerId;
	
	@JsonProperty("departmentId")
	@NotNull(message ="604")
	@NotEmpty(message = "604")
	private String departmentId;
	
	@JsonProperty("positionId")
	@NotNull(message ="60")
	@NotEmpty(message = "")
	private String positionId;
	
	@JsonProperty("rankId")
	@NotNull(message ="")
	@NotEmpty(message = "")
	private String rankId;
	
	@JsonProperty("onBoardDate")
	@NotNull(message ="")
	@NotEmpty(message = "")
	private String onBoardDate;
	
	@JsonProperty("citizenIdentification")
	@NotNull(message ="")
	@NotEmpty(message = "")
	private String citizenIdentification;
	
	@JsonProperty("phoneNumber")
	private String phoneNumber;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("gender")
	@NotNull(message ="")
	@NotEmpty(message = "")
	private String gender;
}
