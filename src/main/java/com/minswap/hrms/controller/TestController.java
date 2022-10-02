package com.minswap.hrms.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.dto.request.BasicRequest;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.exception.model.Pageable;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.TestResponse;
import com.minswap.hrms.service.ServiceTest;

@RestController
@RequestMapping(CommonConstant.ADMIN + "/account")
public class TestController {

  @Autowired
  private ServiceTest serviceTest;

  
    @PostMapping("/test")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<TestResponse, Pageable>> checkAvailability(@RequestBody @Valid BasicRequest request,BindingResult bindingResult) {
        return serviceTest.serviceTest(request.getRequestId());
    }
}
