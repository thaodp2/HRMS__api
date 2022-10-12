package com.minswap.hrms.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;

public interface EmployeeHRService {
  
  ResponseEntity<BaseResponse<EmployeeListResponse, Pageable>> getListEmployee(int page, int limit);

}
