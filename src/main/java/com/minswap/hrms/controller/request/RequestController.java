package com.minswap.hrms.controller.request;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestDetailRespone;
import com.minswap.hrms.service.RequestDetailService;
import com.minswap.hrms.service.impl.RequestDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstant.REQUEST)
public class RequestController {

    @Autowired
    RequestDetailService requestDetailService;

    @GetMapping("")
    public ResponseEntity<BaseResponse<RequestDetailRespone, Void>> getRequestDetail(@RequestParam Long id) {
        return requestDetailService.getEmployeeRequestDetail(id);
    }

}
