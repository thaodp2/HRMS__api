package com.minswap.hrms.service.device;

import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.request.DeviceRequest;
import com.minswap.hrms.request.UpdateDeviceRequest;
import com.minswap.hrms.response.DeviceResponse;
import com.minswap.hrms.response.MasterDataResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;

public interface DeviceService {
    ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceByDeviceType (Long deviceTypeId, Integer status, String deviceName);

    ResponseEntity<BaseResponse<HttpStatus, Void>> assignDevice(AssignRequest assignRequest);

    ResponseEntity<BaseResponse<HttpStatus, Void>> createDevice(DeviceRequest deviceRequest);

    ResponseEntity<BaseResponse<HttpStatus, Void>> updateDevice(UpdateDeviceRequest deviceRequest, Long deviceId);

    ResponseEntity<BaseResponse<HttpStatus, Void>> deleteDevice(Long deviceId);

    ResponseEntity<BaseResponse<DeviceResponse.DetailDeviceResponse, Void>> getDetailDevice(Long deviceId);

    ResponseEntity<BaseResponse<DeviceResponse, Pageable>> searchListDevice(String search, Integer isUser, Long deviceTypeId, Integer page, Integer limit);

    ResponseEntity<BaseResponse<HttpStatus, Void>> isRemainDeviceByDeviceTye(Long deviceTypeId);

    ResponseEntity<BaseResponse<HttpStatus, Void>> returnDevice(Long borrowHistoryId);
}
