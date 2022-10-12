package com.minswap.hrms.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.service.ServiceTest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceTestImpl implements ServiceTest{

  @Override
  public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pagination>> serviceTest(String requestId) {
    
    Pagination pageable = new Pagination();
    pageable.setLimit(20);
    pageable.setPage(13);
    pageable.setTotalRecords(2);
    EmployeeInfoResponse response = new EmployeeInfoResponse();
     ResponseEntity<BaseResponse<EmployeeInfoResponse, Pagination>> responseEntity =
        BaseResponse.ofSucceededOffset(response, pageable);
    return responseEntity;
  }

}
