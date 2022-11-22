package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BenefitBudgetDto {

    public BenefitBudgetDto(Long id, String fullName, Double budget, Double used, Double remain, String requestTypeName) {
        this.id = id;
        this.fullName = fullName;
        this.budget = budget;
        this.used = used;
        this.remain = remain;
        this.requestTypeName = requestTypeName;
    }

    public BenefitBudgetDto(Long id, String fullName, Double budget, Double used, Double remain) {
        this.id = id;
        this.fullName = fullName;
        this.budget = budget;
        this.used = used;
        this.remain = remain;
    }

    private Long id;

    private String fullName;

    private Double budget;

    private Double used;

    private Double remain;

    private String requestTypeName;
}
