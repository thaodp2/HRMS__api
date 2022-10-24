package com.minswap.hrms.controller.hr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.service.EmployeeHRService;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

  @Autowired
  private EmployeeHRService employeeHRService;

  
    @GetMapping("/employee")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getListEmployee(
            @RequestParam int page,
            @RequestParam int limit
            ) {
        return employeeHRService.getListEmployee(page, limit);
    }

    @GetMapping("/employee/{personId}")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(@PathVariable Long personId) {
      return employeeHRService.getDetailEmployee(personId);
    }

  @GetMapping("search/employee")
  public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(
          @RequestParam int page,
          @RequestParam int limit,
          @RequestParam String fullName,
          @RequestParam String email,
          @RequestParam String departmentName,
          @RequestParam String rollNumber,
          @RequestParam String active,
          @RequestParam String positionName

  ) {
    return employeeHRService.getSearchListEmployee(page,limit,fullName,email,departmentName,rollNumber,active,positionName);
  }


  @PutMapping("/employee/{personId}")
  public ResponseEntity<BaseResponse<Void, Void>> changeStatusEmployee(
          @PathVariable Long personId,
          @RequestParam String active) {
    return employeeHRService.changeStatusEmployee(personId,active);
  }
}
