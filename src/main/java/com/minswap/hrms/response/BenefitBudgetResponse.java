package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import lombok.Data;

import java.util.List;

@Data
public class BenefitBudgetResponse {
    @JsonProperty(value = "item")
    private BenefitBudgetDto benefitDto;
    public BenefitBudgetResponse(BenefitBudgetDto benefitDto) {
        this.benefitDto = benefitDto;
    }

    @Data
    public static class BenefitBudgetListResponse{
        @JsonProperty(value = "items")
        private List<BenefitBudgetDto> benefitDto;
        public BenefitBudgetListResponse(List<BenefitBudgetDto> benefitDto) {
            this.benefitDto = benefitDto;
        }
    }
}
