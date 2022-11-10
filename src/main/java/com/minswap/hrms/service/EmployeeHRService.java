package com.minswap.hrms.service;

import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;

public interface EmployeeHRService {

  ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(String rollNumber);

  ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName,String email,Long departmentId,String rollNumber,String status,Long positionId, String managerRoll);

  ResponseEntity<BaseResponse<Void, Void>> exportEmployee();
  ResponseEntity<BaseResponse<Void, Void>> updateEmployee( EmployeeRequest employeeRequest, String rollNumber );

  ResponseEntity<BaseResponse<Void, Void>> createEmployee(EmployeeRequest employeeRequest);

  ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(ChangeStatusEmployeeRequest employeeRequest, String rollNumber );
}
