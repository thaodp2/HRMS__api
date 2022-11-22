package com.minswap.hrms.service.benefitbudget;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Year;

public interface BenefitBudgetService {
    ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> getBenefitBudget(Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Integer month, Year year, String sort, String dir) throws ParseException;
}
