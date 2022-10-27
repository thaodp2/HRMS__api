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
@Table(name = OTBudget.TABLE_NAME)
public class OTBudget {
    public static final String TABLE_NAME = "ot_budget";

    @Id
    @Column(name = "ot_budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otBudgetId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "ot_hours_budget")
    private double otHoursBudget;

    @Column(name = "hours_worked")
    private double numberOfDayOff;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    private int year;
}
