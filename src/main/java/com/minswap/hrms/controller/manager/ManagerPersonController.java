package com.minswap.hrms.controller.manager;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.service.EmployeeHRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CommonConstant.MANAGER + "/")
public class ManagerPersonController {

  @Autowired
  private EmployeeHRService employeeHRService;

  @GetMapping("/employee")
  public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(
          @RequestParam int page,
          @RequestParam int limit,
          @RequestParam (name = "fullName", required = false)String fullName,
          @RequestParam (name = "email", required = false)String email,
          @RequestParam (name = "departmentName", required = false) String departmentName,
          @RequestParam (name = "rollNumber", required = false) String rollNumber,
          @RequestParam (name = "active", required = false) String active,
          @RequestParam (name = "positionName", required = false)String positionName,
          @RequestParam (name = "managerRoll", required = false)String managerRoll

  ) {
    return employeeHRService.getSearchListEmployee(page,limit,fullName,email,departmentName,rollNumber,active,positionName,managerRoll);
  }
}
