package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRDepartmentController {

    @PostMapping
    public ResponseEntity<BaseResponse<EmployeeListResponse, Pageable>> getListEmployee(@RequestParam int page,
                                                                                        @RequestParam int limit) {
        return null;
    }
}
