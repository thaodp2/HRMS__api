package com.minswap.hrms.service.device;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Device;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceRepository;
import com.minswap.hrms.repsotories.RequestRepository;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.request.DeviceRequest;
import com.minswap.hrms.request.UpdateDeviceRequest;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.service.borrowhistory.BorrowHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService{

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    BorrowHistoryService borrowHistoryService;

    @Autowired
    RequestRepository requestRepository;

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceByDeviceType(Long deviceTypeId, Integer status, String search) {
        List<Device> devices;
        if (search != null) {
            devices = deviceRepository.findByDeviceTypeIdAndStatusAndDeviceNameContainsIgnoreCase(deviceTypeId,status,search.trim());
        } else {
            devices = deviceRepository.findByDeviceTypeIdAndStatus(deviceTypeId,status);
        }
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        for (int i = 0; i < devices.size(); i++) {
            MasterDataDto masterDataDto = new MasterDataDto(devices.get(i).getDeviceName() + " - " + devices.get(i).getDeviceCode(), devices.get(i).getDeviceId());
            masterDataDtos.add(masterDataDto);
        }
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> assignDevice(AssignRequest assignRequest) throws ParseException {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        try {
            //create borrow history
            borrowHistoryService.createBorrowHistory(assignRequest);
            //update status device
            Device device = deviceRepository.findById(assignRequest.getDeviceId()).orElse(null);
            if (device != null) {
                device.setStatus(1);
                deviceRepository.save(device);
            }
            //update is assigned in request
            Request request = requestRepository.findById(assignRequest.getRequestId()).orElse(null);
            if (request != null) {
                request.setIsAssigned(1);
                requestRepository.save(request);
            }
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }catch (ParseException p){
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> createDevice(DeviceRequest deviceRequest) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        Optional<Device> deviceFormDB = deviceRepository.findByDeviceCode(deviceRequest.getDeviceCode());
        if (deviceFormDB.isPresent()){
            throw new BaseException(ErrorCode.DUPLICATE_DEVICE_CODE);
        }
        try {
            ModelMapper modelMapper = new ModelMapper();
            Device  device = new Device();
            modelMapper.map(deviceRequest,device);
            device.setStatus(0);
            device.setDeviceId(0L);
            deviceRepository.save(device);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }catch (Exception p){
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateDevice(UpdateDeviceRequest deviceRequest, Long deviceId) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        Optional<Device> deviceByCode = deviceRepository.findByDeviceCode(deviceRequest.getDeviceCode());
        if (deviceByCode.isPresent()){
            throw new BaseException(ErrorCode.DUPLICATE_DEVICE_CODE);
        }
        Optional<Device> deviceById  = deviceRepository.findByDeviceId(deviceId);
        if (!deviceById.isPresent()){
            throw new BaseException(ErrorCode.DUPLICATE_DEVICE_CODE);        }
        try {

            Device device = deviceById.get();
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(deviceRequest, device);
            device.setDeviceId(deviceId);
            deviceRepository.save(device);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }catch (Exception p){
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }


    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> isRemainDeviceByDeviceTye(Long deviceTypeId) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        List<Device> deviceList = deviceRepository.findByDeviceTypeIdAndStatus(deviceTypeId, 0);
        if(deviceList.isEmpty()){
            throw new BaseException(ErrorCode.DO_NOT_ENOUGH_DEVICE_TO_ASSIGN);
        }
        responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }
}
