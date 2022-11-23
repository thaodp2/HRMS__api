package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.util.ExportEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

    @Autowired
    private PersonService personService;

    @Autowired
    private DeviceTypeRepository deviceTypeRepository;

    @GetMapping("/employee/{rollNumber}")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(@PathVariable String rollNumber) {
        return personService.getDetailEmployee(rollNumber);
    }

    @GetMapping("/employee")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(name = "search", required = false) String fullName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "rollNumber", required = false) String rollNumber,
            @RequestParam(name = "active", required = false) String active,
            @RequestParam(name = "positionId", required = false) Long positionId

    ) {
        return personService.getSearchListEmployee(page, limit, fullName, email, departmentId, rollNumber, active, positionId, "");
    }


    @PutMapping("/employee/{rollNumber}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> updateEmployee(
            @RequestBody @Valid EmployeeRequest employeeRequest,
            BindingResult bindingResult,
            @PathVariable String rollNumber) {
        return personService.updateEmployee(employeeRequest, rollNumber);
    }

    @PostMapping("/employee")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> createEmployee(
            @RequestBody @Valid EmployeeRequest employeeRequest,
            BindingResult bindingResult) {
        return personService.createEmployee(employeeRequest);
    }

    @PutMapping("/status/employee/{rollNumber}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(
            @RequestBody @Valid ChangeStatusEmployeeRequest changeStatusEmployeeRequest,
            BindingResult bindingResult,
            @PathVariable String rollNumber) {
        return personService.updateStatusEmployee(changeStatusEmployeeRequest, rollNumber);
    }

    @GetMapping("/employee/export")
    public ResponseEntity<BaseResponse<Void, Void>> exportToExcel(
            HttpServletResponse response,
            @RequestParam(name = "search", required = false) String fullName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "rollNumber", required = false) String rollNumber,
            @RequestParam(name = "positionId", required = false) Long positionId
    ) throws IOException, ParseException {
        List<EmployeeListDto> employeeListDtos = personService.exportEmployee(fullName, email, departmentId, rollNumber, positionId);
        if (!employeeListDtos.isEmpty()) {
            String fileName = "employee";
            ExportEmployee excelExporter = new ExportEmployee(employeeListDtos);
            excelExporter.init(response, fileName);
            excelExporter.exportEmployee(response);
        }
        return null;
    }
}
