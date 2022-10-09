package com.minswap.hrms.service.impl;

import com.minswap.hrms.model.Role;
import com.minswap.hrms.repository.RoleRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.minswap.hrms.exception.model.Pageable;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.TestResponse;
import com.minswap.hrms.service.ServiceTest;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
public class ServiceTestImpl implements ServiceTest{
  @Autowired
  RoleRespository roleRespository;
  @Override
  public ResponseEntity<BaseResponse<List<Role>, Pageable>> serviceTest() {
    
    Pageable pageable = new Pageable();
    pageable.setLimit(20);
    pageable.setPage(0);
    pageable.setRecords(5);
    pageable.setTotalRecords(2);
    ResponseEntity<BaseResponse<List<Role>, Pageable>> responseEntity =
        BaseResponse.ofSucceededOffset(roleRespository.findAll(), pageable);
    return responseEntity;
  }

}
