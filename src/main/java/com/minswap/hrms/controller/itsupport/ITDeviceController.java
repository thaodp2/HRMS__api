package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.request.DeviceRequest;
import com.minswap.hrms.request.DeviceTypeRequest;
import com.minswap.hrms.request.UpdateDeviceRequest;
import com.minswap.hrms.service.device.DeviceService;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping(CommonConstant.ITSUPPORT + "/")
@Validated
public class ITDeviceController {
    @Autowired
    DeviceService deviceService;

    @PostMapping("/assign")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> assignDevice(@RequestBody
                                                                           @Valid AssignRequest assignRequest,
                                                                           BindingResult bindingResult) throws ParseException {
        return deviceService.assignDevice(assignRequest);
    }

    @PostMapping("/device")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> createDevice(@RequestBody
                                                                       @Valid DeviceRequest deviceRequest,
                                                                       BindingResult bindingResult) {
        return deviceService.createDevice(deviceRequest);
    }

    @PutMapping("/device")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateDevice(@RequestBody
                                                                       @Valid UpdateDeviceRequest deviceRequest,
                                                                       BindingResult bindingResult,
                                                                       Long deviceId) {
        return deviceService.updateDevice(deviceRequest, deviceId);
    }
}
