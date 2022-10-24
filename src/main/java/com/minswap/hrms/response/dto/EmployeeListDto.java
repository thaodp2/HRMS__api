package com.minswap.hrms.response.dto;

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
}
