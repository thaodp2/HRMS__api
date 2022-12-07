package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Device;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceRepository;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.DeviceTypeDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class DeviceTypeServiceImpl implements DeviceTypeService {

    @Autowired
    DeviceTypeRepository deviceTypeRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Override
    public ResponseEntity<BaseResponse<DeviceTypeResponse.DeviceTypeDtoResponse, Pageable>> getAllDeviceType(Integer page, Integer limit, String deviceTypeName) {
        Pagination pagination = new Pagination(page, limit);
        List<DeviceType> deviceTypes = null;
        List<DeviceTypeDto> deviceTypeDtos = new ArrayList<>();
        if (deviceTypeName != null) {
            pagination.setTotalRecords(deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(deviceTypeName.trim()).size());
            deviceTypes = deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(deviceTypeName.trim(), new Pagination(page - 1, limit));
        } else {
            pagination.setTotalRecords(deviceTypeRepository.findAll().size());
            deviceTypes = deviceTypeRepository.findAll(new Pagination(page - 1, limit)).getContent();
        }
        if (deviceTypes != null && !deviceTypes.isEmpty()) {
            for (DeviceType deviceType : deviceTypes) {
                if (checkIsAllowDelete(deviceType.getDeviceTypeId())) {
                    deviceTypeDtos.add(new DeviceTypeDto(deviceType.getDeviceTypeId(), deviceType.getDeviceTypeName(),1));
                }else {
                    deviceTypeDtos.add(new DeviceTypeDto(deviceType.getDeviceTypeId(), deviceType.getDeviceTypeName(),0));
                }
            }
        }

        DeviceTypeResponse.DeviceTypeDtoResponse response = new DeviceTypeResponse.DeviceTypeDtoResponse(deviceTypeDtos);
        ResponseEntity<BaseResponse<DeviceTypeResponse.DeviceTypeDtoResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, pagination);
        return responseEntity;
    }

    public boolean checkIsAllowDelete(Long deviceTypeId) {
        List<Device> devices = deviceRepository.findByDeviceTypeIdAndStatus(deviceTypeId, 1);
        if (devices.isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean checkDuplicateDeviceType(List<String> deviceTypeName) {
        Set<String> store = new HashSet<>();
        for (String item : deviceTypeName) {
            if (store.add(item.toLowerCase()) == false){
                return true;
            }
        }
        for (String item : deviceTypeName) {
            List<DeviceType> deviceTypes = deviceTypeRepository.findByDeviceTypeNameIgnoreCase(item);
            if (deviceTypes.size() != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean checkItemEmpty(List<String> deviceTypeName) {
        for (String item : deviceTypeName) {
            if (item == null || item.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> createDeviceType(List<String> deviceTypeName) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        if (deviceTypeName != null && !deviceTypeName.isEmpty()) {
            if (checkDuplicateDeviceType(deviceTypeName)) {
                throw new BaseException(ErrorCode.DUPLICATE_DEVICE_TYPE);
            }
            if (checkItemEmpty(deviceTypeName)) {
                throw new BaseException(ErrorCode.DEVICE_TYPE_NULL_OR_EMPTY);
            }
            for (String item : deviceTypeName) {
                DeviceType deviceType = new DeviceType(item.trim());
                deviceTypeRepository.save(deviceType);
            }
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        } else {
            throw new BaseException(ErrorCode.INVALID_PARAMETERS);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> editDeviceType(Long id, String deviceTypeName) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        if (deviceTypeName != null && !deviceTypeName.trim().isEmpty()) {
            DeviceType deviceType = deviceTypeRepository.findById(id).orElse(null);
            if (deviceType != null) {
                List<DeviceType> deviceTypes = deviceTypeRepository.findByDeviceTypeNameIgnoreCase(deviceTypeName.trim());
                deviceTypes.remove(deviceType);
                if (deviceTypes.size() != 0) {
                    throw new BaseException(ErrorCode.DUPLICATE_DEVICE_TYPE);
                } else {
                    deviceType.setDeviceTypeName(deviceTypeName.trim());
                    deviceTypeRepository.save(deviceType);
                    responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
                }
            } else {
                throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
            }
        } else {
            throw new BaseException(ErrorCode.DEVICE_TYPE_NULL_OR_EMPTY);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType(Long id) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        DeviceType deviceType = deviceTypeRepository.findById(id).orElse(null);
        if (deviceType != null) {
            deviceTypeRepository.deleteById(id);
            List<Device> deviceList = deviceRepository.findByDeviceTypeId(id);
            deviceRepository.deleteAll(deviceList);
            responseEntity = BaseResponse.ofSucceeded(null);
        } else {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceType(String search) {
        List<DeviceType> deviceTypes;
        if (search != null) {
            deviceTypes = deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(search.trim());
        } else {
            deviceTypes = deviceTypeRepository.findAll();
        }
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        for (int i = 0; i < deviceTypes.size(); i++) {
            MasterDataDto masterDataDto = new MasterDataDto(deviceTypes.get(i).getDeviceTypeName(), deviceTypes.get(i).getDeviceTypeId());
            masterDataDtos.add(masterDataDto);
        }
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

}
