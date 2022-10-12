package com.minswap.hrms.controller.manager;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.BasicRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.service.ServiceTest;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
public class TestController {

  @Autowired
  private ServiceTest serviceTest;

  
    @PostMapping("/test")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pagination>> checkAvailability(@RequestBody @Valid BasicRequest request,BindingResult bindingResult) {
        return serviceTest.serviceTest(request.getRequestId());
    }
}
