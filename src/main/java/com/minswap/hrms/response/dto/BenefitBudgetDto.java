package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BenefitBudgetDto {

    public BenefitBudgetDto(Long id, String rollNumber, String fullName, Double budget, Double used, Double remainOfYear, String requestTypeName) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.budget = budget;
        this.used = used;
        this.remainOfYear = remainOfYear;
        this.requestTypeName = requestTypeName;
    }

    public BenefitBudgetDto(Long id, String rollNumber, String fullName, Double budget, Double used, Double remainOfMonth, Double remainOfYear) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.budget = budget;
        this.used = used;
        this.remainOfMonth = remainOfMonth;
        this.remainOfYear = remainOfYear;
    }

    private Long id;

    private String rollNumber;

    private String fullName;

    private Double budget;

    private Double used;

    private Double remainOfMonth;

    private Double remainOfYear;

    private String requestTypeName;
}
