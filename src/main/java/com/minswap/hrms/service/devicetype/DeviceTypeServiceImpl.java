package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.*;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.DeviceTypeDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.security.oauth2.CurrentUser;
import com.minswap.hrms.service.person.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DeviceTypeServiceImpl implements DeviceTypeService {

    @Autowired
    DeviceTypeRepository deviceTypeRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PersonService personService;

    @Autowired
    PersonRepository personRepository;

    @Override
    public ResponseEntity<BaseResponse<DeviceTypeResponse.DeviceTypeDtoResponse, Pageable>> getAllDeviceType(Integer page, Integer limit, String deviceTypeName) {
        Pagination pagination = new Pagination(page, limit);
        List<DeviceType> deviceTypes = null;
        List<DeviceTypeDto> deviceTypeDtos = new ArrayList<>();
        if (deviceTypeName != null) {
            pagination.setTotalRecords(deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCaseAndStatus(deviceTypeName.trim(),0).size());
            deviceTypes = deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCaseAndStatus(deviceTypeName.trim(),0, PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "deviceTypeId")));
        } else {
            pagination.setTotalRecords(deviceTypeRepository.findByStatus(0).size());
            deviceTypes = deviceTypeRepository.findByStatus(0,PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "deviceTypeId")));
        }
        if (deviceTypes != null && !deviceTypes.isEmpty()) {
            for (DeviceType deviceType : deviceTypes) {
                if (checkIsAllowDelete(deviceType.getDeviceTypeId())) {
                    deviceTypeDtos.add(new DeviceTypeDto(deviceType.getDeviceTypeId(), deviceType.getDeviceTypeName(),deviceType.getStatus(),1));
                }else {
                    deviceTypeDtos.add(new DeviceTypeDto(deviceType.getDeviceTypeId(), deviceType.getDeviceTypeName(),deviceType.getStatus(),0));
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
            if (checkItemEmpty(deviceTypeName)) {
                throw new BaseException(ErrorCode.DEVICE_TYPE_NULL_OR_EMPTY);
            }
            if (checkDuplicateDeviceType(deviceTypeName)) {
                throw new BaseException(ErrorCode.DUPLICATE_DEVICE_TYPE);
            }
            for (String item : deviceTypeName) {
                DeviceType deviceType = new DeviceType(item.trim(), 0);
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
    public ResponseEntity<BaseResponse<Void, Void>> deleteDeviceType(Long id, UserPrincipal userPrincipal) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        DeviceType deviceType = deviceTypeRepository.findById(id).orElse(null);
        if (deviceType != null) {
            deviceType.setStatus(1);
            deviceTypeRepository.save(deviceType);
            List<Device> deviceList = deviceRepository.findByDeviceTypeId(id);
            if(deviceList != null && !deviceList.isEmpty()){
                for (Device device: deviceList) {
                    device.setStatus(2);
                    deviceRepository.save(device);
                }
            }

            List<Request> requests = requestRepository.getListRequestWhenDeviceTypeDelete(deviceType.getDeviceTypeId());
            Person currentUser = personService.getPersonInforByEmail(userPrincipal.getEmail());
            List<Notification> notificationList = new ArrayList<>();
            if(requests != null && !requests.isEmpty()){
                for (Request request: requests) {
                    Date dateToReject = new Date();
                    Date date = new Date();
                    dateToReject.setTime(dateToReject.getTime() - CommonConstant.MILLISECOND_7_HOURS);
                    request.setStatus("Rejected");
                    request.setMaximumTimeToRollback(dateToReject);
                    requestRepository.save(request);
                    date.setTime(date.getTime() + CommonConstant.MILLISECOND_7_HOURS);

                    Notification notificationToEmp = new Notification("asks you to choose another device type because the device you requested no longer exists!",
                            0, null, 0, currentUser.getPersonId(), request.getPersonId(), date);
                    notificationList.add(notificationToEmp);
                    Person person = personRepository.findById(request.getPersonId()).orElse(null);
                    if(person != null && person.getManagerId() != null) {
                        Notification notificationToManager = new Notification("informs that the device type you requested to borrow to "+person.getFullName() +" - "+person.getRollNumber()  +" staff no longer exists!",
                                0, null, 0, currentUser.getPersonId(), person.getManagerId(), date);
                        notificationList.add(notificationToManager);
                    }
                }
                notificationRepository.saveAll(notificationList);
            }
            responseEntity = BaseResponse.ofSucceeded(null);
        } else {
            throw new BaseException(ErrorCode.NOT_FOUND_DEVICE_TYPE);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceType(String search) {
        List<DeviceType> deviceTypes = new ArrayList<>();
        if (search != null) {
            deviceTypes = deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCaseAndStatus(search.trim(), 0);
        } else {
            deviceTypes = deviceTypeRepository.findByStatus(0);
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
