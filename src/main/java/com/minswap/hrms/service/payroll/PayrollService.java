package com.minswap.hrms.service.payroll;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.PayrollResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface PayrollService {

    ResponseEntity<BaseResponse<PayrollResponse, Void>> getDetailPayroll(int month, int year, Long personId);

    ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(int month, int year, Long personId);
}
