package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeListDto {

    @JsonProperty("id")
    private Long personId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("rollNumber")
    private String rollNumber;

    @JsonProperty("isActive")
    private String active;

    @JsonProperty("positionName")
    private String positionName;
    
    @JsonProperty("userName")
    private String userName;
    
    public int getActive() {
    	return active == null ? 0 : Integer.parseInt(active);
    }

	public EmployeeListDto(Long personId, String fullName, String email, String departmentName, String rollNumber,
			String active, String positionName) {
		super();
		this.personId = personId;
		this.fullName = fullName;
		this.email = email;
		this.departmentName = departmentName;
		this.rollNumber = rollNumber;
		this.active = active;
		this.positionName = positionName;
	}
}
