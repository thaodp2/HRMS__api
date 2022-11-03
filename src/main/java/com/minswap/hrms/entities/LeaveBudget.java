package com.minswap.hrms.entities;

import com.minswap.hrms.util.YearAttributeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.Year;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = LeaveBudget.TABLE_NAME)
public class LeaveBudget {

    public static final String TABLE_NAME = "leave_budget";

    @Id
    @Column(name = "leave_budget_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveBudgetId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "leave_budget")
    private double leaveBudget;

    @Column(name = "number_of_day_off")
    private double numberOfDayOff;

    @Column(name = "remain_day_off")
    private double remainDayOff;

    @Column(name = "year")
    @Convert(
            converter = YearAttributeConverter.class
    )
    private Year year;

    @Column(name = "request_type_id")
    private Long requestTypeId;
}
