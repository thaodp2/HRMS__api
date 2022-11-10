package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.service.EmployeeHRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

  @Autowired
  private EmployeeHRService employeeHRService;

  @Autowired
  private DeviceTypeRepository deviceTypeRepository;
    
    @GetMapping("/employee/{rollNumber}")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(@PathVariable String rollNumber) {
      return employeeHRService.getDetailEmployee(rollNumber);
    }

  @GetMapping("/employee")
  public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(
          @RequestParam int page,
          @RequestParam int limit,
          @RequestParam (name = "fullName", required = false)String fullName,
          @RequestParam (name = "email", required = false)String email,
          @RequestParam (name = "departmentId", required = false) Long departmentId,
          @RequestParam (name = "rollNumber", required = false) String rollNumber,
          @RequestParam (name = "active", required = false) String active,
          @RequestParam (name = "positionId", required = false)Long positionId

  ) {
    return employeeHRService.getSearchListEmployee(page,limit,fullName,email,departmentId,rollNumber,active,positionId, "");
  }


  @PutMapping("/employee/{rollNumber}")
  @ServiceProcessingValidateAnnotation
  public ResponseEntity<BaseResponse<Void, Void>> updateEmployee(
		  @RequestBody @Valid EmployeeRequest employeeRequest ,
		  BindingResult bindingResult,
          @PathVariable String rollNumber) {
    return employeeHRService.updateEmployee(employeeRequest, rollNumber);
  }

  @PostMapping("/employee")
  @ServiceProcessingValidateAnnotation
  public ResponseEntity<BaseResponse<Void, Void>> createEmployee(
		  @RequestBody @Valid EmployeeRequest employeeRequest , 
		  BindingResult bindingResult) {
    return employeeHRService.createEmployee(employeeRequest);
  }
  @PutMapping("/status/employee/{rollNumber}")
  @ServiceProcessingValidateAnnotation
  public ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(
          @RequestBody @Valid ChangeStatusEmployeeRequest changeStatusEmployeeRequest ,
          BindingResult bindingResult,
          @PathVariable String rollNumber) {
    return employeeHRService.updateStatusEmployee(changeStatusEmployeeRequest, rollNumber);
  }
//  @GetMapping("/employee/export")
//  public ResponseEntity<BaseResponse<Void, Void>> exportToExcel(
//          HttpServletResponse response
//  ) throws IOException {
//    response.setContentType("application/octet-stream");
//    String headerKey = "Content-Disposition";
//
//    DateFormat dateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
//    String currentDateTime = dateFormat.format(new Date());
//    String fileName = "employees_" + currentDateTime + ".xlsx";
//    String headerValue = "attachment; filename=" + fileName;
//
//    response.setHeader(headerKey, headerValue);
//    List<DeviceType> deviceTypeList = deviceTypeRepository.findAll();
//    ExcelExporter excelExporter = new ExcelExporter(deviceTypeList);
//    excelExporter.export(response);
//
//    return null;
//  }
}
