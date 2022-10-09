package com.minswap.hrms.service;

import com.minswap.hrms.model.Role;
import org.springframework.http.ResponseEntity;

import com.minswap.hrms.exception.model.Pageable;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.TestResponse;

import java.util.List;

public interface ServiceTest {
  
  ResponseEntity<BaseResponse<List<Role>, Pageable>> serviceTest();

}
