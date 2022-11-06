package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DeviceTypeServiceImpl implements DeviceTypeService {

    @Autowired
    DeviceTypeRepository deviceTypeRepository;

    @Override
    public ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> getAllDeviceType(Integer page, Integer limit, String deviceTypeName) {
        Pagination pagination = new Pagination(page - 1, limit);
        List<DeviceType> deviceTypes = null;
        if(deviceTypeName != null){
            pagination.setTotalRecords(deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(deviceTypeName).size());
            deviceTypes = deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(deviceTypeName, pagination);
        }else {
            pagination.setTotalRecords(deviceTypeRepository.findAll().size());
            deviceTypes = deviceTypeRepository.findAll(pagination).getContent();
        }
        DeviceTypeResponse response = new DeviceTypeResponse(deviceTypes);
        ResponseEntity<BaseResponse<DeviceTypeResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createDeviceType(String deviceTypeName) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        List<DeviceType> deviceTypes = deviceTypeRepository.findByDeviceTypeNameIgnoreCase(deviceTypeName);
        if(deviceTypes.size() != 0){
            // fails
            throw new BaseException(ErrorCode.DUPLICATE_DEVICE_TYPE);
        }else {
            //create
            DeviceType deviceType = new DeviceType(deviceTypeName);
            deviceTypeRepository.save(deviceType);
            responseEntity = BaseResponse.ofSucceeded(null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> editDeviceType(Long id, String deviceTypeName) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        DeviceType deviceType = deviceTypeRepository.findById(id).orElse(null);
        if(deviceType != null){
            List<DeviceType> deviceTypes = deviceTypeRepository.findByDeviceTypeNameIgnoreCase(deviceTypeName);
            deviceTypes.remove(deviceType);
            if(deviceTypes.size() != 0) {
                throw new BaseException(ErrorCode.DUPLICATE_DEVICE_TYPE);
            }else {
                deviceType.setDeviceTypeName(deviceTypeName);
                deviceTypeRepository.save(deviceType);
                responseEntity = BaseResponse.ofSucceeded(null);
            }
        }else {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType(Long id) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        DeviceType deviceType = deviceTypeRepository.findById(id).orElse(null);
        if(deviceType != null){
            deviceTypeRepository.deleteById(id);
            responseEntity = BaseResponse.ofSucceeded(null);
        }else {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<List<MasterDataDto>, Pageable>> getMasterDataDeviceType() {
        List<DeviceType> deviceTypes = deviceTypeRepository.findAll();
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        for (int i = 0; i < deviceTypes.size(); i++) {
            MasterDataDto masterDataDto = new MasterDataDto(deviceTypes.get(i).getDeviceTypeName(), deviceTypes.get(i).getDeviceTypeId());
            masterDataDtos.add(masterDataDto);
        }
        ResponseEntity<BaseResponse<List<MasterDataDto>, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(masterDataDtos, null);
        return responseEntity;
    }
}
