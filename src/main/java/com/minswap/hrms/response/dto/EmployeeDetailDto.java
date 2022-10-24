package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailDto {

    @JsonProperty("id")
    private Long personId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("citizenIdentification")
    private String citizenIdentification;

    @JsonProperty("address")
    private String address;

    @JsonProperty("rollNumber")
    private String rollNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("departmentName")
    private String departmentName;

    @JsonProperty("positionName")
    private String positionName;

    @JsonProperty("rankName")
    private String rankName;

    @JsonProperty("onBoardDate")
    private String onBoardDate;

    @JsonProperty("status")
    private String status;

    @JsonProperty("role")
    private String role;

    @JsonProperty("managerName")
    private String managerName;
}
