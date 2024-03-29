package com.minswap.hrms.controller;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.benefitbudget.BenefitBudgetService;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.text.ParseException;
import java.time.Year;

@RestController
@Validated
public class BenefitBudgetController {
    @Autowired
    private BenefitBudgetService benefitBudgetService;

    @Autowired
    PersonService personService;

    @GetMapping("/benefit-budget")
    public ResponseEntity<BaseResponse<BenefitBudgetResponse.BenefitBudgetListResponse, Pageable>> getMyBenefitBudget(
            @RequestParam Long requestTypeId,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) Year year,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir,
            @CurrentUser UserPrincipal userPrincipal
    ){
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return benefitBudgetService.getBenefitBudget(null, personId, null, null, requestTypeId != CommonConstant.REQUEST_TYPE_ID_OF_OT ? null:CommonConstant.REQUEST_TYPE_ID_OF_OT, null, month, year, sort, dir);
    }
}
