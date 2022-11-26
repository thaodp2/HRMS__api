package com.minswap.hrms.controller;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.SignatureProfileRequest;
import com.minswap.hrms.response.BenefitBudgetResponse;
import com.minswap.hrms.service.benefitbudget.BenefitBudgetService;
import com.minswap.hrms.service.signatureProfile.SignatureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.text.ParseException;
import java.time.Year;

@RestController
@Validated
public class SignatureProfileController {
    @Autowired
    SignatureProfileService signatureProfileService;

    @PostMapping("/register-signature")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> registerSignatureEmployee(
            @RequestBody @Valid SignatureProfileRequest signatureProfileRequest,
            BindingResult bindingResult) {

        return signatureProfileService.registerSignature(signatureProfileRequest);
    }
}
