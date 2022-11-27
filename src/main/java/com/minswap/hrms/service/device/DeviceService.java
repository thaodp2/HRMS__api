package com.minswap.hrms.service.device;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.response.MasterDataResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface DeviceService {
    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceByDeviceType (Long deviceTypeId, Integer status, String deviceName);

    ResponseEntity<BaseResponse<HttpStatus, Void>> assignDevice(AssignRequest assignRequest);

    ResponseEntity<BaseResponse<HttpStatus, Void>> isRemainDeviceByDeviceTye(Long deviceTypeId);

    ResponseEntity<BaseResponse<HttpStatus, Void>> returnDevice(Long borrowHistoryId);
}
