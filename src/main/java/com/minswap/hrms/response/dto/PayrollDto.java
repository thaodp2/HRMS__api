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

   private Long basicSalary;

   private Long otSalary;

   private Long bonus;

   private Long fineAmount;

   private Long totalSalary;

   private Integer month;

   private Integer year;
}
