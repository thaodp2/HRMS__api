package com.minswap.hrms.service;

import org.springframework.http.ResponseEntity;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;

public interface ServiceTest {
  
  ResponseEntity<BaseResponse<EmployeeInfoResponse, Pagination>> serviceTest(String text);

}
