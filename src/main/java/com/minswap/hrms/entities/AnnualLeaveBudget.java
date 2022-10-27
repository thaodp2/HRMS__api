package com.minswap.hrms.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Year;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = AnnualLeaveBudget.TABLE_NAME)
public class AnnualLeaveBudget {

    public static final String TABLE_NAME = "annual_leave_budget";

    @Id
    @Column(name = "annual_leave_budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long annualLeaveBudgetId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "annual_leave_budget")
    private double annualLeaveBudget;

    @Column(name = "number_of_day_off")
    private double numberOfDayOff;

    @Column(name = "remain_day_off")
    private double remainDayOff;

    @Column(name = "year")
    private int year;

}
