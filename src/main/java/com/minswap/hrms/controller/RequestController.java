package com.minswap.hrms.controller;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.RequestResponse;
import com.minswap.hrms.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("/employee/{employeeId}/request")
    public ResponseEntity<BaseResponse<RequestResponse.RequestListResponse, Pageable>> getListEmployee(@PathVariable Integer employeeId, @RequestParam int page, @RequestParam int limit) {
        return requestService.getMyRequest(employeeId,page, limit);
    }
}
