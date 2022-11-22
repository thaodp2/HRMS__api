package com.minswap.hrms.service.leavebudget;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Year;

public interface LeaveBudgetService {
    void createLeaveBudget();

    void updateLeaveBudgetEachMonth();
}
