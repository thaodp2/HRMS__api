package com.minswap.hrms.entities;

import com.minswap.hrms.util.YearAttributeConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Year;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = OTBudget.TABLE_NAME)
public class OTBudget {
    public static final String TABLE_NAME = "ot_budget";

    public OTBudget(Long personId, double otHoursBudget, double hoursWorked,
                    double timeRemainingOfMonth, double timeRemainingOfYear, int month, Year year) {
        this.personId = personId;
        this.otHoursBudget = otHoursBudget;
        this.hoursWorked = hoursWorked;
        this.timeRemainingOfMonth = timeRemainingOfMonth;
        this.timeRemainingOfYear = timeRemainingOfYear;
        this.month = month;
        this.year = year;
    }

    @Id
    @Column(name = "ot_budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long otBudgetId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "ot_hours_budget")
    private double otHoursBudget;

    @Column(name = "hours_worked")
    private double hoursWorked;

    @Column(name = "time_remaining_of_month")
    private double timeRemainingOfMonth;

    @Column(name = "time_remaining_of_year")
    private double timeRemainingOfYear;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    @Convert(
            converter = YearAttributeConverter.class
    )
    private Year year;
}
