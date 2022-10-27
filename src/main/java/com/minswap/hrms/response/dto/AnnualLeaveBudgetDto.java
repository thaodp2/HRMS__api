package com.minswap.hrms.response.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class AnnualLeaveBudgetDto {

    public AnnualLeaveBudgetDto(double annualLeaveBudget,
                                double numberOfDayOff,
                                double remainDayOff) {
        this.annualLeaveBudget = annualLeaveBudget;
        this.numberOfDayOff = numberOfDayOff;
        this.remainDayOff = remainDayOff;
    }

    private double annualLeaveBudget;

    private double numberOfDayOff;

    private double remainDayOff;
}
