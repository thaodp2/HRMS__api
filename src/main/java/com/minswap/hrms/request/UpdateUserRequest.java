package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.Date;

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
    @Pattern(regexp = "^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$", message = "609")
    private String phoneNumber;

    @JsonProperty("gender")
    private int gender;

    @JsonProperty("dateOfBirth")
    @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])", message = "Invalid dateOfBirth")
    private String dateOfBirth;

    @JsonProperty("avatar")
    private String avatarImg;

}
