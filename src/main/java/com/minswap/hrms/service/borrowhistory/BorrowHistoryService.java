package com.minswap.hrms.service.borrowhistory;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.BorrowHistoryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Year;

public interface BorrowHistoryService {
    void createBorrowHistory (AssignRequest assignRequest) throws ParseException;

    ResponseEntity<BaseResponse<BorrowHistoryResponse, Pageable>> getBorrowHistoryList(Long managerId, Long personId, Integer page, Integer limit, Long deviceTypeId, String search, String sort, String dir);

}
