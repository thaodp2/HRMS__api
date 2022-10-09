package com.minswap.hrms.controller;

import javax.validation.Valid;

import com.minswap.hrms.model.Role;
import com.minswap.hrms.repository.RoleRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.dto.request.BasicRequest;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.exception.model.Pageable;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.TestResponse;
import com.minswap.hrms.service.ServiceTest;

import java.util.List;

@RestController
@RequestMapping(CommonConstant.ADMIN + "/account")
public class TestController {

  @Autowired
  private ServiceTest serviceTest;

    @GetMapping ("/role")
    public ResponseEntity<BaseResponse<List<Role>, Pageable>> checkAvailability() {
      return serviceTest.serviceTest();
    }
}
