package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequest {

    @JsonProperty("personId")
    private Long personId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("citizenIdentification")
    private String citizenIdentification;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("onBoardDate")
    private String onBoardDate;

    @JsonProperty("rankId")
    private String rankId;

    @JsonProperty("departmentId")
    private String departmentId;

    @JsonProperty("managerId")
    private Integer managerId;

    @JsonProperty("gender")
    private int gender;

    @JsonProperty("rollNumber")
    private String rollNumber;

    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("status")
    private String status;

    @JsonProperty("positionId")
    private Long positionId;

    @JsonProperty("annualLeaveBudget")
    private Double annualLeaveBudget;


}
