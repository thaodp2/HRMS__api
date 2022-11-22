package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.LeaveBudgetResponse;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import com.minswap.hrms.service.benefitbudget.BenefitBudgetService;
import com.minswap.hrms.service.leavebudget.LeaveBudgetServiceImpl;
import com.minswap.hrms.service.otbudget.OTBudgetService;
import com.minswap.hrms.util.ExportLeaveBudget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.text.ParseException;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.HR + "/")
@Validated
public class HRBenefitBudgetController {
    @Autowired
    private BenefitBudgetService benefitBudgetService;

    @Autowired
    private OTBudgetService otBudgetService;

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
        return benefitBudgetService.getBenefitBudget(null, null, page, limit, requestTypeId, search, month, year, sort, dir);
    }
//    @GetMapping("/leave-budget/export")
//    public ResponseEntity<BaseResponse<HttpStatus, Void>> exportToExcel(
//            HttpServletResponse response,
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) Year year
//    ) throws IOException, ParseException {
//        List<LeaveBudgetDto> leaveBudgetDtos = leaveBudgetService.getQueryForLeaveBudgetList(CommonConstant.ALL, false, null, null, null, null, null, search, year, true);
//        if (leaveBudgetDtos != null && leaveBudgetDtos.size() > 0) {
//            String fileName = "leave-budget-in-" + (year == null ? Year.now().toString() : year.toString());
//            ExportLeaveBudget excelExporter = new ExportLeaveBudget(leaveBudgetDtos);
//            excelExporter.init(response, fileName);
//            excelExporter.exportLeaveBudget(response);
//        }
//        return null;
//    }
}
