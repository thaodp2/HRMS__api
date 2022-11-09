package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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

    public UpdateUserRequest(Long personId, String fullName, String address, String citizenIdentification, String phoneNumber, String email, String onBoardDate, String rankId, String departmentId, Integer managerId, int gender, String rollNumber, String dateOfBirth, String status) {
        this.personId = personId;
        this.fullName = fullName;
        this.address = address;
        this.citizenIdentification = citizenIdentification;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.onBoardDate = onBoardDate;
        this.rankId = rankId;
        this.departmentId = departmentId;
        this.managerId = managerId;
        this.gender = gender;
        this.rollNumber = rollNumber;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }
}
