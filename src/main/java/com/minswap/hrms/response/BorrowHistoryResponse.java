package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.BorrowHistoryDto;
import com.minswap.hrms.response.dto.DepartmentDto;
import lombok.Data;

import java.util.List;
@Data
public class BorrowHistoryResponse {
    @JsonProperty(value = "items")
    private List<BorrowHistoryDto> borrowHistoryDtos;
    public BorrowHistoryResponse(List<BorrowHistoryDto> borrowHistoryDtos) {
        this.borrowHistoryDtos = borrowHistoryDtos;
    }
}
