package com.minswap.hrms.controller;


import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.service.officeTime.OfficeTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/office-time")
public class OfficeTimeController {
    @Autowired
    private OfficeTimeService officeTimeService;

    @GetMapping("")
    public ResponseEntity<BaseResponse<OfficeTime, Void>> getOfficeTime() throws Exception {
        return officeTimeService.getOfficeTime();
    }

}
