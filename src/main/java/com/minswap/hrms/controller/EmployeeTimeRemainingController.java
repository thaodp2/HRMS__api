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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@Validated
public class EmployeeTimeRemainingController {

    @Autowired
    EmployeeTimeRemainingService employeeTimeRemainingService;

    @GetMapping("request/remaining-time")
    public ResponseEntity<BaseResponse<EmployeeTimeRemainingResponse, Void>> getEmployeeRemainingTime(
                                                        @RequestParam Long requestTypeId,
                                                        @RequestParam (required = false) int month,
                                                        @RequestParam int year) {
        return employeeTimeRemainingService.getEmployeeRemainingTime(requestTypeId, month, year);
    }
}
