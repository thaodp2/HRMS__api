package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class SignatureProfileRequest extends BasicRequest {

    @JsonProperty("personId")
    @NotNull(message = "613")
    @NotEmpty(message = "613")
    private String personId;

    @JsonProperty("privateKeySignature")
    @NotNull(message = "614")
    @NotEmpty(message = "614")
    private String privateKeySignature;
}
