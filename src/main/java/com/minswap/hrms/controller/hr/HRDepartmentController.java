package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.DepartmentRequest;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import com.minswap.hrms.service.department.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(CommonConstant.HR)
public class HRDepartmentController {

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/department")
    public ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> getListDepartment(@RequestParam Integer page,
                                                                                       @RequestParam Integer limit,
                                                                                       @RequestParam(required = false) String departmentName) {
        return departmentService.getListDepartment(page, limit, departmentName);
    }
    @PostMapping("/department")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> createDepartment(@RequestBody
                                                                     @Valid
                                                                     DepartmentRequest departmentRequest,
                                                                     BindingResult bindingResult) {
        return departmentService.createDepartment(departmentRequest.getDepartmentName());
    }

    @PutMapping("/department/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> editDepartment(@RequestBody
                                                                   @Valid
                                                                   DepartmentRequest departmentRequest,
                                                                   BindingResult bindingResult,
                                                                   @PathVariable Long id) {
        return departmentService.editDepartment(id, departmentRequest.getDepartmentName());
    }

    @DeleteMapping("department/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> deleteDepartment(@PathVariable Long id) {
        return departmentService.deleteDepartment(id);
    }


}
