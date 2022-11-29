package com.minswap.hrms.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PayrollDto {

   private Long personId;

   private String personName;

   private String rollNumber;

   private Double totalWork;

   private Double actualWork;

   private Double basicSalary;

   private Double otSalary;

   private Double fineAmount;

   private Double salaryBonus;

   private Double tax;

   private Double socialInsurance;

   private Double actuallyReceived;

   public PayrollDto() {
   }
}
