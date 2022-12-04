package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.SignatureProfileDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckEachSubordinateDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignatureProfileResponse {
    @JsonProperty("item")
    private List<SignatureProfileDto> signatureProfileDto;


}
