package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.service.payroll.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payroll")
public class SalaryController {

    @Autowired
    PayrollService payrollService;
    @GetMapping("")
    public ResponseEntity<BaseResponse<PayrollResponse, Void>> getPayroll(@RequestParam int month,
                                                                          @RequestParam int year){
        return payrollService.getDetailPayroll(month, year);
    }

    @GetMapping("/send")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(@RequestParam int month,
                                                                     @RequestParam int year){
        return payrollService.sendPayrollToEmail(month, year);
    }

}
