package com.minswap.hrms.service.benefitbudget;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.time.Year;
import java.util.List;

public interface BenefitBudgetService {
    ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> getBenefitBudget(Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Integer month, Year year, String sort, String dir) throws ParseException;
    List<BenefitBudgetDto> getBenefitBudgetList(Long managerId, Long personId, Integer page, Integer limit, Long requestTypeId, String search, Integer month, Year year, String sort, String dir);
}
