package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.service.department.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DepartmentController {
    @Autowired
    DepartmentService departmentService;

    @GetMapping("/department-master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDepartment() {
        return departmentService.getMasterDataDepartment();
    }
}
