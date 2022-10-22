package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.TimeCheckListResponse;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.service.timeCheck.TimeCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class MyTimeCheckController {

    @Autowired
    TimeCheckService timeCheckService;

    @GetMapping("/time-check")
    public ResponseEntity<BaseResponse<TimeCheckListResponse, Pageable>> searchRequest(@RequestParam Long personId,
                                                                                       @RequestParam(required = false) String startDate,
                                                                                       @RequestParam(required = false) String endDate,
                                                                                       @RequestParam(defaultValue = "0") Integer page,
                                                                                       @RequestParam(defaultValue = "10") Integer limit) throws Exception {
     return timeCheckService.getMyTimeCheck(personId, startDate, endDate, page, limit);

    }
}
