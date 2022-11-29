package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class UpdateSecureCodeRequest extends BasicRequest{

    @JsonProperty("currentSecureCode")
    private String currentSecureCode;

    @JsonProperty("newSecureCode")
    private String newSecureCode;

    @JsonProperty("confirmSecureCode")
    private String confirmSecureCode;

}
