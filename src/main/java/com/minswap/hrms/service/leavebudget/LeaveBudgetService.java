package com.minswap.hrms.service.leavebudget;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Year;

public interface LeaveBudgetService {
    ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetOfAllEmployee(Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException;

    ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetOfSubordinate(Long managerId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException;

    ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getMyLeaveBudget(Long personId, Integer page, Integer limit, Long requestTypeId, String search, Year year) throws ParseException;

    public void createLeaveBudget();

    public void updateLeaveBudgetEachMonth();
}
