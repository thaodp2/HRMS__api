package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.EmployeeTimeRemainingDto;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeTimeRemainingResponse {

    @JsonProperty(value = "item")
    private EmployeeTimeRemainingDto employeeTimeRemainingDto;

    public EmployeeTimeRemainingResponse(EmployeeTimeRemainingDto employeeTimeRemainingDto) {
        this.employeeTimeRemainingDto = employeeTimeRemainingDto;
    }
}
