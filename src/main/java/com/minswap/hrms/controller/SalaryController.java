package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.UpdateSecureCodeRequest;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.payroll.PayrollService;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payroll")
public class SalaryController {

    @Autowired
    PayrollService payrollService;

    @Autowired
    PersonService personService;

    @GetMapping("")
    public ResponseEntity<BaseResponse<PayrollResponse, Void>> getPayroll(@CurrentUser UserPrincipal userPrincipal,
                                                                          @RequestParam int month,
                                                                          @RequestParam int year,
                                                                          @RequestParam String currentSecureCode){
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return payrollService.getDetailPayroll(month, year, personId, currentSecureCode);
    }

    @GetMapping("/send")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(@RequestParam int month,
                                                                             @RequestParam int year,
                                                                             @CurrentUser UserPrincipal userPrincipal,
                                                                             @RequestParam String currentSecureCode) {
        return payrollService.sendPayrollToEmail(userPrincipal,month, year, currentSecureCode);
    }

}
