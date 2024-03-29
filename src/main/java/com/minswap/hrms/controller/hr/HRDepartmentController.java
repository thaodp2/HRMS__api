package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.CreateDepartmentRequest;
import com.minswap.hrms.request.DepartmentRequest;
import com.minswap.hrms.response.DepartmentResponse;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import com.minswap.hrms.service.department.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(CommonConstant.HR)
public class HRDepartmentController {

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/department")
    public ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>>
                            getListDepartment(@RequestParam @Min(1) Integer page,
                                              @RequestParam @Min(0) Integer limit,
                                              @RequestParam (required = false) String search,
                                              @RequestParam (required = false) String sort,
                                              @RequestParam (required = false) String dir) {
        return departmentService.getListDepartment(page, limit, search, sort, dir);
    }
    @PostMapping("/department")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> createDepartment(@RequestBody
                                                                     @Valid
                                                                     CreateDepartmentRequest createDepartmentRequest,
                                                                     BindingResult bindingResult) {
        return departmentService.createDepartment(createDepartmentRequest);
    }

    @PutMapping("/department/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<Void, Void>> editDepartment(@RequestBody
                                                                   @Valid
                                                                   DepartmentRequest departmentRequest,
                                                                   BindingResult bindingResult,
                                                                   @PathVariable Long id) {
        return departmentService.editDepartment(id, departmentRequest);
    }

    @DeleteMapping("department/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> deleteDepartment(@PathVariable Long id) {
        return departmentService.deleteDepartment(id);
    }

    @GetMapping("department/{id}")
    public ResponseEntity<BaseResponse<DepartmentResponse, Void>> getDepartmentDetail(@PathVariable Long id) {
        return departmentService.getDepartmentDetail(id);
    }
}
