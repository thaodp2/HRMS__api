package com.minswap.hrms.controller;

import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.TimeRemainingRequest;
import com.minswap.hrms.response.EmployeeTimeRemainingResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.timeRemaining.EmployeeTimeRemainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
public class EmployeeTimeRemainingController {

    @Autowired
    EmployeeTimeRemainingService employeeTimeRemainingService;

    @GetMapping("request/remaining-time")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<EmployeeTimeRemainingResponse, Void>>
                                    getEmployeeRemainingTime(@RequestBody
                                                             @Valid TimeRemainingRequest timeRemainingRequest,
                                                             BindingResult bindingResult) {
        return employeeTimeRemainingService.getEmployeeRemainingTime(timeRemainingRequest);
    }
}
