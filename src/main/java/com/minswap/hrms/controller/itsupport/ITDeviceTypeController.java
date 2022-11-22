package com.minswap.hrms.controller.itsupport;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.DeviceTypeRequest;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(CommonConstant.ITSUPPORT + "/")
@Validated
public class ITDeviceTypeController {
    @Autowired
    DeviceTypeService deviceTypeService;

    @GetMapping("/device-type")
    public ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> getAllDeviceType(@RequestParam @Min(1) Integer page,
                                                                                        @RequestParam @Min(0) Integer limit,
                                                                                        @RequestParam (required = false) String search) {
        return deviceTypeService.getAllDeviceType(page,limit,search);
    }

    @PostMapping("/device-type")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> createDeviceType(@RequestBody
                                                                     @Valid DeviceTypeRequest deviceTypeRequest,
                                                                           BindingResult bindingResult) {
        return deviceTypeService.createDeviceType(deviceTypeRequest.getDeviceTypeNames());
    }

    @PutMapping("/device-type/{id}")
    @ServiceProcessingValidateAnnotation
    public ResponseEntity<BaseResponse<HttpStatus, Void>> editDepartment(@RequestBody
                                                                   @Valid DeviceTypeRequest deviceTypeRequest,
                                                                   BindingResult bindingResult,
                                                                   @PathVariable Long id) {
        return deviceTypeService.editDeviceType(id, deviceTypeRequest.getDeviceTypeName());
    }

    @DeleteMapping("/device-type/{id}")
    public ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType(@PathVariable Long id) {
        return deviceTypeService.deleteDeviceType(id);
    }
}
