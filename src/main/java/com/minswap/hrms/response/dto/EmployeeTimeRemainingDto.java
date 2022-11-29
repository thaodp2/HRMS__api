package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeTimeRemainingDto {

    public EmployeeTimeRemainingDto(double otTimeRemainingOfMonth,
                                    double otTimeRemainingOfYear) {
        this.otTimeRemainingOfMonth = otTimeRemainingOfMonth;
        this.otTimeRemainingOfYear = otTimeRemainingOfYear;
    }

    public EmployeeTimeRemainingDto(double otTimeRemainingOfYear) {
        this.otTimeRemainingOfYear = otTimeRemainingOfYear;
    }

    private double otTimeRemainingOfMonth;
    private double otTimeRemainingOfYear;
}
