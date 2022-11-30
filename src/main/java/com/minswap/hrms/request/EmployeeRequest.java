package com.minswap.hrms.request;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest extends BasicRequest{
	@JsonProperty("fullName")
	@NotNull(message ="600")
	@NotEmpty(message = "600")
//	@Pattern(regexp = "^[a-zA-Z0-9 ]{3,20}$", message = "600")
	private String fullName;
	
	@JsonProperty("dateOfBirth")
	@NotNull(message ="601")
	@NotEmpty(message = "601")
	private String dateOfBirth;
	
	@JsonProperty("managerId")
	@NotNull(message ="602")
	private Long managerId;
	
	@JsonProperty("departmentId")
	@NotNull(message ="603")
	private Long departmentId;
	
	@JsonProperty("positionId")
	@NotNull(message ="604")
	private Long positionId;
	
	@JsonProperty("rankId")
	@NotNull(message ="605")
	private Long rankId;
	
	@JsonProperty("onBoardDate")
	@NotNull(message ="606")
	@NotEmpty(message = "606")
//	@Pattern(regexp = "((0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-[12]\\d{3})", message = "601")
	private String onBoardDate;
	
	@JsonProperty("citizenIdentification")
	@NotNull(message ="607")
	@NotEmpty(message = "607")
	@Pattern(regexp = "^[0-9]{9}$|^[0-9]{12}$", message = "617")
	private String citizenIdentification;
	
	@JsonProperty("phoneNumber")
	@Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$", message = "609")
	private String phoneNumber;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("gender")
	@NotNull(message ="610")
	private Integer gender;

	@JsonProperty("isActive")
	private String active;

	@Column(name = "salaryBasic")
	@Min(value = 0, message = "611")
	private Double salaryBasic;

	@Column(name = "salaryBonus")
	@Min(value = 0, message = "612")
	private Double salaryBonus;

	@JsonProperty("isManager")
	private Integer isManager;
}
