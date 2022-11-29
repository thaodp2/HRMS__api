package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.BorrowHistory;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.response.dto.BorrowHistoryDto;
import com.minswap.hrms.response.dto.DepartmentDto;
import lombok.Data;

import java.util.List;
@Data
public class BorrowHistoryResponse {
    @JsonProperty(value = "item")
    private BorrowHistoryDto borrowHistory;
    public BorrowHistoryResponse(BorrowHistoryDto borrowHistory) {
        this.borrowHistory = borrowHistory;
    }

    @Data
    public static class BorrowHistoryListResponse{
        @JsonProperty(value = "items")
        private List<BorrowHistoryDto> borrowHistoryDtos;
        public BorrowHistoryListResponse(List<BorrowHistoryDto> borrowHistoryDtos) {
            this.borrowHistoryDtos = borrowHistoryDtos;
        }
    }
}
