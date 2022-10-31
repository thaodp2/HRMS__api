package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OTBudgetDto {
    public OTBudgetDto(double otHoursBudget, double hoursWorked, double timeRemaining) {
        this.otHoursBudget = otHoursBudget;
        this.hoursWorked = hoursWorked;
        this.timeRemaining = timeRemaining;
    }

    private double otHoursBudget;

    private double hoursWorked;

    private double timeRemaining;
}
