package com.minswap.hrms.service;

import org.springframework.http.ResponseEntity;

import com.minswap.hrms.exception.model.Pageable;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.TestResponse;

public interface ServiceTest {
  
  ResponseEntity<BaseResponse<TestResponse, Pageable>> serviceTest(String text);

}
