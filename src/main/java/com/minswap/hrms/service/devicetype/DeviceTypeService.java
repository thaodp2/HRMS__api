package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DeviceTypeService {
    ResponseEntity<BaseResponse<DeviceTypeResponse.DeviceTypeDtoResponse, Pageable>> getAllDeviceType (Integer page,
                                                                                  Integer limit,
                                                                                  String deviceTypeName);
    ResponseEntity<BaseResponse<HttpStatus, Void>> createDeviceType (List<String> deviceTypeName);

    ResponseEntity<BaseResponse<HttpStatus, Void>> editDeviceType (Long id,
                                                             String deviceTypeName);
    ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType (Long id, UserPrincipal userPrincipal);

    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceType (String search);
}
