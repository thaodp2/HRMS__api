package com.minswap.hrms.service.deviceType;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.DeviceTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DeviceTypeService {
    ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> getAllDeviceType (Integer page,
                                                                                  Integer limit,
                                                                                  String deviceTypeName);
    ResponseEntity<BaseResponse<Void, Void>> createDeviceType (String deviceTypeName);

    ResponseEntity<BaseResponse<Void, Void>> editDeviceType (Long id,
                                                             String deviceTypeName);
    ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType (Long id);
}
