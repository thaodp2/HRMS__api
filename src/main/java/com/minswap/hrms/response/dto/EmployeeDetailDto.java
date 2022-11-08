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
    private int gender;

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

    @JsonProperty("departmenId")
    private Long departmenId;

    @JsonProperty("positionId")
    private Long positionId;

    @JsonProperty("rankId")
    private Long rankId;

    @JsonProperty("onBoardDate")
    private String onBoardDate;

    @JsonProperty("isActive")
    private String status;

    @JsonProperty("role")
    private String role;

    @JsonProperty("managerId")
    private Long managerId;
    
    public int getStatus() {
    	return status == null ? 0 : Integer.parseInt(status);
    }
}
