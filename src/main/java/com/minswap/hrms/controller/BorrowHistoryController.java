package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BorrowHistoryResponse;
import com.minswap.hrms.service.borrowhistory.BorrowHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;

@RestController
@Validated
public class BorrowHistoryController {
    @Autowired
    private BorrowHistoryService borrowHistoryService;

    @GetMapping("/borrow-history")
    public ResponseEntity<BaseResponse<BorrowHistoryResponse, Pageable>> getMyBorrowHistory(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam(required = false) Long deviceTypeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir) {
        Long personId = Long.valueOf(2);
        return borrowHistoryService.getBorrowHistoryList(null,personId,page,limit,deviceTypeId,search,sort, dir);
    }
}
