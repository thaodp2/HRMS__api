package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.service.benefitbudget.BenefitBudgetService;
import com.minswap.hrms.service.otbudget.OTBudgetService;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.util.ExportBenefitBudget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    ){
        return benefitBudgetService.getBenefitBudget(null, null, page, limit, requestTypeId, search, month, year, sort, dir);
    }

    @GetMapping("/benefit-budget/export")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> exportToExcel(
            HttpServletResponse response,
            @RequestParam Long requestTypeId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) Year year
    ) throws IOException{
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        List<BenefitBudgetDto> benefitBudgetDtoList = benefitBudgetService.getBenefitBudgetList(null, null, null, null, requestTypeId, search, month, year, null, null);
        if (benefitBudgetDtoList != null && benefitBudgetDtoList.size() > 0) {
            String monthString = "";
            if(requestTypeId == CommonConstant.REQUEST_TYPE_ID_OF_OT){
                monthString = month == null ? (java.time.LocalDateTime.now().getMonthValue() + "_") : (month + "_");
            }
            String fileName = "benefit-budget-in-" + monthString + (year == null ? Year.now().toString() : year.toString());
            ExportBenefitBudget excelExporter = new ExportBenefitBudget(benefitBudgetDtoList);
            excelExporter.init(response, fileName);
            excelExporter.exportBenefitBudget(response);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }else {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null, "Don't have data to download!");
        }
        return responseEntity;
    }
}
