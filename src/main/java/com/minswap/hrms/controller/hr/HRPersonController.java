package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.EmployeeUpdateRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.util.ExportEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

    @Autowired
    private PersonService personService;

    @Value("${template.dir}")
    private String templateDir;

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
            @RequestParam(name = "isActive", required = false) String active,
            @RequestParam(name = "positionId", required = false) Long positionId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String dir

    ) {
        return personService.getSearchListEmployee(page, limit, fullName, email, departmentId, fullName, active, positionId, "", sort, dir);
    }

    @GetMapping("/employee/master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getEmployeeMasterData(
            @RequestParam(name = "search", required = false) String search
    ) {
        return personService.getEmployeeMasterData(search);
    }


    @PutMapping("/employee/{rollNumber}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> updateEmployee(
            @RequestBody @Valid EmployeeUpdateRequest employeeRequest,
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
    public ResponseEntity<BaseResponse<HttpStatus, Void>> exportToExcel(
            HttpServletResponse response,
            @RequestParam(name = "search", required = false) String fullName,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "rollNumber", required = false) String rollNumber,
            @RequestParam(name = "positionId", required = false) Long positionId
    ) throws IOException, ParseException {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        List<EmployeeListDto> employeeListDtos = personService.exportEmployee(fullName, email, departmentId, rollNumber, positionId);
        if (!employeeListDtos.isEmpty()) {
            String fileName = "employee";
            ExportEmployee excelExporter = new ExportEmployee(employeeListDtos);
            excelExporter.init(response, fileName);
            excelExporter.exportEmployee(response);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }else {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null, "Don't have data to download!");
        }
        return responseEntity;
    }

    @GetMapping("/template/export")
    public ResponseEntity<InputStreamResource> exportToExcel(
            HttpServletResponse response
    ) throws IOException{
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=template_import_employee.xlsx");
        String localDir =templateDir;
        InputStreamResource resource = new InputStreamResource(new FileInputStream(localDir));
        return ResponseEntity.ok().body(resource);
    }

    @PostMapping("/employee/import")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(@Valid @RequestParam MultipartFile file) throws IOException {
        return personService.importExcel(file);
    }
}
