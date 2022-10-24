package com.minswap.hrms.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;

public interface EmployeeHRService {
  
  ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getListEmployee(int page, int limit);

  ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(Long personID);

  ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName,String email,String departmentName,String rollNumber,String status,String positionName);

  ResponseEntity<BaseResponse<Void, Void>> changeStatusEmployee(Long personId, String active);
}
