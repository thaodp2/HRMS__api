package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import lombok.Data;

import java.util.List;

@Data
public class LeaveBudgetResponse {
    @JsonProperty(value = "item")
    private LeaveBudgetDto leaveBudgetDto;
    public LeaveBudgetResponse(LeaveBudgetDto leaveBudgetDto) {
        this.leaveBudgetDto = leaveBudgetDto;
    }

    @Data
    public static class LeaveBudgetListResponse{
        @JsonProperty(value = "items")
        private List<LeaveBudgetDto> leaveBudgetDto;
        public LeaveBudgetListResponse(List<LeaveBudgetDto> leaveBudgetDto) {
            this.leaveBudgetDto = leaveBudgetDto;
        }
    }
}
