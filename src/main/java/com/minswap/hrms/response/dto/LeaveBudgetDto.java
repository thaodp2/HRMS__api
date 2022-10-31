package com.minswap.hrms.response.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
public class LeaveBudgetDto {

    public LeaveBudgetDto(double leaveBudget,
                          double numberOfDayOff,
                          double remainDayOff) {
        this.leaveBudget = leaveBudget;
        this.numberOfDayOff = numberOfDayOff;
        this.remainDayOff = remainDayOff;
    }

    private double leaveBudget;

    private double numberOfDayOff;

    private double remainDayOff;
}
