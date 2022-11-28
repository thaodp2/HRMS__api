package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.request.DeviceRequest;
import com.minswap.hrms.request.DeviceTypeRequest;
import com.minswap.hrms.request.UpdateDeviceRequest;
import com.minswap.hrms.response.DeviceResponse;
import com.minswap.hrms.service.device.DeviceService;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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

    @PutMapping("/device/{deviceId}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateDevice(@RequestBody
                                                                       @Valid UpdateDeviceRequest deviceRequest,
                                                                       BindingResult bindingResult,
                                                                       @PathVariable Long deviceId) {
        return deviceService.updateDevice(deviceRequest, deviceId);
    }

    @DeleteMapping("/device/{deviceId}")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> deleteDevice(@PathVariable Long deviceId) {
        return deviceService.deleteDevice(deviceId);
    }

    @GetMapping("/device")
    public ResponseEntity<BaseResponse<DeviceResponse, Pageable>> searchListDevice(@RequestParam (required = false) String search,
                                                                                   @RequestParam (required = false) Integer isUsed,
                                                                                   @RequestParam (required = false) Long deviceTypeId,
                                                                                   @RequestParam (defaultValue = "1") Integer page,
                                                                                   @RequestParam (defaultValue = "10") Integer limit) {
        return deviceService.searchListDevice(search, isUsed, deviceTypeId, page, limit);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<BaseResponse<DeviceResponse.DetailDeviceResponse, Void>> getDetailDevice(@PathVariable Long deviceId) {
        return deviceService.getDetailDevice(deviceId);
    }

}
