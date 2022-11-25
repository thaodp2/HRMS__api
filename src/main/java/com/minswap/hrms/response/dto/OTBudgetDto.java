package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OTBudgetDto {
    public OTBudgetDto(double otHoursBudget,
                       double hoursWorked,
                       double otHoursRemainOfMonth,
                       double otHoursRemainOfYear) {
        this.otHoursBudget = otHoursBudget;
        this.hoursWorked = hoursWorked;
        this.otHoursRemainOfMonth = otHoursRemainOfMonth;
        this.otHoursRemainOfYear = otHoursRemainOfYear;
    }

    private double otHoursBudget;

    private double hoursWorked;

    private double otHoursRemainOfMonth;

    private double otHoursRemainOfYear;

}
