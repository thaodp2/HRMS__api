package com.minswap.hrms.controller.hr;

import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import com.minswap.hrms.util.ExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.service.EmployeeHRService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

  @Autowired
  private EmployeeHRService employeeHRService;

  @Autowired
  private DeviceTypeRepository deviceTypeRepository;

  
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

  @GetMapping("/employee/export")
  public ResponseEntity<BaseResponse<Void, Void>> exportToExcel(
          HttpServletResponse response
  ) throws IOException {
    response.setContentType("application/octet-stream");
    String headerKey = "Content-Disposition";

    DateFormat dateFormat = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
    String currentDateTime = dateFormat.format(new Date());
    String fileName = "employees_" + currentDateTime + ".xlsx";
    String headerValue = "attachment; filename=" + fileName;

    response.setHeader(headerKey, headerValue);
    List<DeviceType> deviceTypeList = deviceTypeRepository.findAll();
    ExcelExporter excelExporter = new ExcelExporter(deviceTypeList);
    excelExporter.export(response);

    return null;
  }
}
