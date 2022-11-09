package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.service.department.DepartmentService;
import com.minswap.hrms.service.position.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PositionController {
    @Autowired
    PositionService positionService;

    @GetMapping("/position-by-departmentId-master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataPositionByDepartmentId(@RequestParam Long departmentId) {
        return positionService.getMasterDataPositionByDepartmentId(departmentId);
    }
}
