package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import com.minswap.hrms.service.leavebudget.LeaveBudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.text.ParseException;
import java.time.Year;

@RestController
@RequestMapping(CommonConstant.HR + "/")
@Validated
public class HRLeaveBudgetController {
    @Autowired
    private LeaveBudgetService leaveBudgetService;

    @GetMapping("/leave-budget")
    public ResponseEntity<BaseResponse<LeaveBudgetResponse.LeaveBudgetListResponse, Pageable>> getLeaveBudgetOfAllEmployee(
            @RequestParam @Min(1) Integer page,
            @RequestParam Integer limit,
            @RequestParam (required = false) Long requestTypeId,
            @RequestParam (required = false) String search,
            @RequestParam (required = false) Year year
            ) throws ParseException {
        return leaveBudgetService.getLeaveBudgetOfAllEmployee(page, limit, requestTypeId, search, year);
    }
}
