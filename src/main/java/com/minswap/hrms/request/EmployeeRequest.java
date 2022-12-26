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
@NoArgsConstructor
public class EmployeeRequest extends BasicRequest{
	public EmployeeRequest(String fullName, String dateOfBirth, Long managerId, Long departmentId, Long positionId, Long rankId, String citizenIdentification, String phoneNumber, String address, Integer gender, String active, Double salaryBasic, Double salaryBonus, Integer isManager) {
		this.fullName = fullName;
		this.dateOfBirth = dateOfBirth;
		this.managerId = managerId;
		this.departmentId = departmentId;
		this.positionId = positionId;
		this.rankId = rankId;
		this.citizenIdentification = citizenIdentification;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.gender = gender;
		this.active = active;
		this.salaryBasic = salaryBasic;
		this.salaryBonus = salaryBonus;
		this.isManager = isManager;
	}

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
