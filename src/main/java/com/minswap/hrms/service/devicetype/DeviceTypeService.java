package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DeviceTypeService {
    ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> getAllDeviceType (Integer page,
                                                                                  Integer limit,
                                                                                  String deviceTypeName);
    ResponseEntity<BaseResponse<Void, Void>> createDeviceType (String deviceTypeName);

    ResponseEntity<BaseResponse<Void, Void>> editDeviceType (Long id,
                                                             String deviceTypeName);
    ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType (Long id);

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceType (String search);
}
