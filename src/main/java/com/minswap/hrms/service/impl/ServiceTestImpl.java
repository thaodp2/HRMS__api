package com.minswap.hrms.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.minswap.hrms.exception.model.Pageable;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.TestResponse;
import com.minswap.hrms.service.ServiceTest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ServiceTestImpl implements ServiceTest{

  @Override
  public ResponseEntity<BaseResponse<TestResponse, Pageable>> serviceTest(String requestId) {
    
    Pageable pageable = new Pageable();
    pageable.setLimit(20);
    pageable.setPage(13);
    pageable.setRecords(5);
    pageable.setTotalRecords(2);
    TestResponse response = new TestResponse();
    response.setRequestId(requestId);
    response.setTestAccount("abc test");
     ResponseEntity<BaseResponse<TestResponse, Pageable>> responseEntity =
        BaseResponse.ofSucceededOffset(response, pageable);
    return responseEntity;
  }

}
