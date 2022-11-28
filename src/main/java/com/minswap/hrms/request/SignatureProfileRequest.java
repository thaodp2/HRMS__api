package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SignatureProfileRequest extends BasicRequest {

    @JsonProperty("roll_number")
    @NotNull(message = "613")
    @NotEmpty(message = "613")
    private String rollNumber;

    @JsonProperty("id_signature")
    @NotNull(message = "614")
    @NotEmpty(message = "614")
    private String idSignature;
}
