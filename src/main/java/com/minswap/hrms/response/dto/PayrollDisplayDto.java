package com.minswap.hrms.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PayrollDisplayDto {

    private Long personId;

    private String personName;

    private String rollNumber;

    private Double totalWork;

    private Double actualWork;

    private String basicSalary;

    private String otSalary;

    private String fineAmount;

    private String salaryBonus;

    private String tax;

    private String socialInsurance;

    private String actuallyReceived;

    public PayrollDisplayDto() {
    }
}
