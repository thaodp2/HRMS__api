package com.minswap.hrms.controller;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.service.devicetype.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DeviceTypeController {
    @Autowired
    DeviceTypeService deviceTypeService;

    @GetMapping("/device-type-master-data")
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceType() {
        return deviceTypeService.getMasterDataDeviceType();
    }
}
