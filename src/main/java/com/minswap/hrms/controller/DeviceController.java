package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;

    @GetMapping("/device-by-deviceType-master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceByDeviceType(@RequestParam Long deviceTypeId,
                                                                                                      @RequestParam(required = false) String search) {
        return deviceService.getMasterDataDeviceByDeviceType(deviceTypeId, 0, search);
    }

    @GetMapping("/check-remaining-device")
    public ResponseEntity<BaseResponse<HttpStatus, Void>> isRemainDeviceByDeviceTye(@RequestParam Long deviceTypeId) {
        return deviceService.isRemainDeviceByDeviceTye(deviceTypeId);
    }


}
