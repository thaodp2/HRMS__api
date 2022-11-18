package com.minswap.hrms.controller.hr;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.OfficeTimeRequest;
import com.minswap.hrms.service.officeTime.OfficeTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(CommonConstant.HR + "/office-time")
@Validated
public class HROfficeTimeController {

    @Autowired
    OfficeTimeService officeTimeService;

    @PutMapping("")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateOfficeTime(@RequestBody
                                                                           @Valid OfficeTimeRequest officeTimeRequest,
                                                                           BindingResult bindingResult) throws Exception {
        return officeTimeService.updateOfficeTime(officeTimeRequest);
    }
}
