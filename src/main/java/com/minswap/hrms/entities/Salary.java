package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Year;

@Entity
@Data
@Table(name = Salary.TABLE_NAME)
public class Salary {

    public static final String TABLE_NAME = "salary";

    @Id
    @Column(name = "salary_id")
    private Long salaryId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "actual_work")
    private Double actualWork;

    @Column(name = "total_work")
    private Double totalWork;

    @Column(name = "basic_salary")
    private String basicSalary;

    @Column(name = "ot_salary")
    private String otSalary;

    @Column(name = "fine_amount")
    private String fineAmount;

    @Column(name = "bonus")
    private String bonus;

    @Column(name = "social_insurance")
    private String socialInsurance;

    @Column(name = "tax")
    private String tax;

    @Column(name = "actually_received")
    private String actuallyReceived;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    private Year year;


}
