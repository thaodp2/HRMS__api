package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.payroll.PayrollService;
import com.minswap.hrms.service.person.PersonService;
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

    @Autowired
    PersonService personService;

    @GetMapping("")
    public ResponseEntity<BaseResponse<PayrollResponse, Void>> getPayroll(@CurrentUser UserPrincipal userPrincipal,
                                                                          @RequestParam int month,
                                                                          @RequestParam int year){
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return payrollService.getDetailPayroll(month, year, personId);
    }

    @GetMapping("/send")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(@CurrentUser UserPrincipal userPrincipal,
                                                                             @RequestParam int month,
                                                                             @RequestParam int year){
        Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
        return payrollService.sendPayrollToEmail(month, year, personId);
    }

}
