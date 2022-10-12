package com.minswap.hrms.controller;

import javax.validation.Valid;

import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.BasicRequest;
import com.minswap.hrms.response.dto.PersonDto;
import com.minswap.hrms.service.EmployeeHRService;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
public class TestController {

  @Autowired
  private EmployeeHRService serviceTest;

  
    @PostMapping("/test")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<PersonDto, Pagination>> checkAvailability(@RequestBody @Valid BasicRequest request,BindingResult bindingResult) {
        return null;
    }
}
