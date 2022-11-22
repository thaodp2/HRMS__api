package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import com.minswap.hrms.service.benefitbudget.BenefitBudgetService;
import com.minswap.hrms.service.leavebudget.LeaveBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.text.ParseException;
import java.time.Year;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
@Validated
public class ManagerBenefitBudgetController {
    @Autowired
    private BenefitBudgetService benefitBudgetService;

    @GetMapping("/benefit-budget")
    public ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> getBenefitBudgetOfSubordinate(
            @RequestParam @Min(1) Integer page,
            @RequestParam @Min(0) Integer limit,
            @RequestParam Long requestTypeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) Year year,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir
    ) throws ParseException {
        Long managerId = Long.valueOf(6);
        return benefitBudgetService.getBenefitBudget(managerId, null, page, limit, requestTypeId, search, month, year, sort, dir);
    }
}
