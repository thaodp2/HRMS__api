package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.entities.Rank;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.repsotories.RankRepository;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.EmployeeUpdateRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.util.ExportEmployee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRPersonController {

    @Autowired
    private PersonService personService;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private RankRepository rankRepository;

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

        //test
//        List<Department> departmentList = departmentRepository.findAll();
        List<Position> positionList = positionRepository.findAll();
        List<Rank> rankList = rankRepository.findAll();
        int maxRow = Math.max(3,Math.max(positionList.size(),rankList.size()));

        InputStream inputStream = new FileInputStream(new File(localDir));
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(1);
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            sheet.removeRow(row);
        }

        int rowCount = 1;
        for (int i = 0; i < maxRow; i++) {
            Row row = sheet.createRow(rowCount++);
        }

//        for (int i = 0; i < departmentList.size(); i++) {
//            Row row = sheet.getRow(i+1);
//            Cell cell = row.createCell(0);
//            cell.setCellValue(departmentList.get(i).getDepartmentId());
//            cell = row.createCell(1);
//            cell.setCellValue(departmentList.get(i).getDepartmentName());
//        }

        for (int i = 0; i < positionList.size(); i++) {
            Row row = sheet.getRow(i+1);
            Cell cell = row.createCell(0);
            cell.setCellValue(positionList.get(i).getPositionName());
            cell = row.createCell(1);
            cell.setCellValue(positionList.get(i).getPositionId());
            cell = row.createCell(2);
            cell.setCellValue(positionList.get(i).getDepartmentId());
        }

        for (int i = 0; i < rankList.size(); i++) {
            Row row = sheet.getRow(i+1);
            Cell cell = row.createCell(4);
            cell.setCellValue(rankList.get(i).getRankName());
            cell = row.createCell(5);
            cell.setCellValue(rankList.get(i).getRankId());
        }

        Row row = sheet.getRow(1);
        Cell cell = row.createCell(7);
        cell.setCellValue("Male");
        cell = row.createCell(8);
        cell.setCellValue(1);
        cell = row.createCell(10);
        cell.setCellValue("Yes");
        cell = row.createCell(11);
        cell.setCellValue(1);
        cell = row.createCell(13);
        cell.setCellValue("Active");
        cell = row.createCell(14);
        cell.setCellValue("1");

        row = sheet.getRow(2);
        cell = row.createCell(7);
        cell.setCellValue("Female");
        cell = row.createCell(8);
        cell.setCellValue(0);
        cell = row.createCell(10);
        cell.setCellValue("No");
        cell = row.createCell(11);
        cell.setCellValue(0);
        cell = row.createCell(13);
        cell.setCellValue("InActive");
        cell = row.createCell(14);
        cell.setCellValue("0");

        row = sheet.getRow(3);
        cell = row.createCell(7);
        cell.setCellValue("Other");
        cell = row.createCell(8);
        cell.setCellValue(-1);


        FileOutputStream fileOut = new FileOutputStream(localDir);
        workbook.write(fileOut);
        fileOut.close();


        InputStreamResource resource = new InputStreamResource(new FileInputStream(localDir));
        return ResponseEntity.ok().body(resource);
    }

    @PostMapping("/employee/import")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(@Valid @RequestParam MultipartFile file) throws IOException {
        return personService.importExcel(file);
    }
    
    @GetMapping("/employee/total")
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getTotalListEmployee() {
        return personService.getTotalListEmployee();
    }
}
