package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeTimeRemainingDto {

    public EmployeeTimeRemainingDto(Double otTimeRemainingOfMonth,
                                    Double otTimeRemainingOfYear) {
        this.otTimeRemainingOfMonth = otTimeRemainingOfMonth;
        this.otTimeRemainingOfYear = otTimeRemainingOfYear;
    }

    public EmployeeTimeRemainingDto(Double timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    private Double timeRemaining;
    private Double otTimeRemainingOfMonth;
    private Double otTimeRemainingOfYear;
}
