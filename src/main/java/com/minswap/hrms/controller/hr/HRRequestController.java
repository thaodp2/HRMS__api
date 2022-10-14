package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.EmployeeHRService;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CommonConstant.HR + "/")
public class HRRequestController {
    @Autowired
    private RequestService requestService;


    @GetMapping("/request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getListEmployee(@RequestParam int page, @RequestParam int limit) {
        return requestService.getAllRequest(page, limit);
    }
}
