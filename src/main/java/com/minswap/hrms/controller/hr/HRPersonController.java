package com.minswap.hrms.controller.hr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.service.EmployeeHRService;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

  @Autowired
  private EmployeeHRService employeeHRService;

  
    @GetMapping("/employee")
    public ResponseEntity<BaseResponse<EmployeeListResponse, Pageable>> getListEmployee(@RequestParam int page,@RequestParam int limit) {
        return employeeHRService.getListEmployee(page, limit);
    }
}
