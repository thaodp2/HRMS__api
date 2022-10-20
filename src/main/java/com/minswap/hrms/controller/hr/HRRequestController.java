package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.EmployeeHRService;
import com.minswap.hrms.service.request.RequestService;
import org.checkerframework.checker.regex.qual.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.RegEx;
import javax.validation.constraints.Pattern;
import java.util.Date;

@RestController
@RequestMapping(CommonConstant.HR + "/")
@Validated
public class HRRequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("/leave-benefit-request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllLeaveBenefitRequest(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String createDateFrom,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String createDateTo,
            @RequestParam (required = false) Long requestTypeId) {
        if(createDateFrom == null && createDateTo == null && requestTypeId == null){
            return requestService.getAllLeaveBenefitRequest(page,limit,false,createDateFrom,createDateTo,requestTypeId);
        }else {
            return requestService.getAllLeaveBenefitRequest(page,limit,true,createDateFrom,createDateTo,requestTypeId);
        }
    }

    @GetMapping("/device-request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getAllDeviceRequest(
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateFrom") String createDateFrom,
            @RequestParam (required = false) @Pattern(regexp = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid createDateTo") String createDateTo,
            @RequestParam (required = false) Long requestTypeId) {
        if(createDateFrom == null && createDateTo == null && requestTypeId == null){
            return requestService.getAllDeviceRequest(page,limit,false,createDateFrom,createDateTo,requestTypeId);
        }else {
            return requestService.getAllDeviceRequest(page,limit,true,createDateFrom,createDateTo,requestTypeId);
        }
    }
}
