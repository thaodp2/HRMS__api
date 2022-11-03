package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeTimeRemainingDto {

    public EmployeeTimeRemainingDto(double timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    private double timeRemaining;
}
