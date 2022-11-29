package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minswap.hrms.response.dto.PayrollDisplayDto;
import com.minswap.hrms.response.dto.PayrollDto;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayrollResponse {

   private PayrollDisplayDto payrollDisplayDto;

    public PayrollResponse(PayrollDisplayDto payrollDisplayDto) {
        this.payrollDisplayDto = payrollDisplayDto;
    }
}
