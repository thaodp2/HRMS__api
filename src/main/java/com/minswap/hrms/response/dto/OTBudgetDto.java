package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OTBudgetDto {
    public OTBudgetDto(double otHoursBudget, double hoursWorked) {
        this.otHoursBudget = otHoursBudget;
        this.hoursWorked = hoursWorked;
    }

    private double otHoursBudget;

    private double hoursWorked;

}
