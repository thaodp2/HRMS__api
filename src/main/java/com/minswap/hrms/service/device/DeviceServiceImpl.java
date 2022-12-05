package com.minswap.hrms.service.device;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.*;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.*;
import com.minswap.hrms.request.AssignRequest;
import com.minswap.hrms.request.DeviceRequest;
import com.minswap.hrms.request.UpdateDeviceRequest;
import com.minswap.hrms.response.DeviceResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.DeviceDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.borrowhistory.BorrowHistoryService;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    BorrowHistoryService borrowHistoryService;

    @Autowired
    BorrowHistoryRepository borrowHistoryRepository;

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PersonService personService;

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDeviceByDeviceType(Long deviceTypeId, Integer status, String search) {
        List<Device> devices;
        if (search != null) {
            devices = deviceRepository.findByDeviceTypeIdAndStatusAndDeviceNameContainsIgnoreCase(deviceTypeId, status, search.trim());
        } else {
            devices = deviceRepository.findByDeviceTypeIdAndStatus(deviceTypeId, status);
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
    public ResponseEntity<BaseResponse<HttpStatus, Void>> assignDevice(AssignRequest assignRequest, UserPrincipal userPrincipal) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        try {
            //create borrow history
            BorrowHistory borrowHistory = borrowHistoryService.createBorrowHistory(assignRequest);

            Device device = deviceRepository.findById(assignRequest.getDeviceId()).orElse(null);
            Request request = requestRepository.findById(assignRequest.getRequestId()).orElse(null);
            if (device != null && request != null) {
                //update status device
                device.setStatus(1);
                deviceRepository.save(device);

                //update is assigned in request
                request.setIsAssigned(1);
                requestRepository.save(request);

                //send a notification to person is assigned
                //fake id of it support is 2 (current user)
//                Long currentUser = Long.valueOf(2);
                Long currentUser = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
                Date currentDate = DateTimeUtil.getCurrentTime();
                currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                Notification notification = new Notification("just assigned you a device " + device.getDeviceName() + " - " + device.getDeviceCode() + "!",
                        0, "emp-self-service/device-history/detail/" + borrowHistory.getBorrowHistoryId(), 0, currentUser, request.getPersonId(), currentDate);
                notificationRepository.save(notification);

                responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
            } else {
                responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
            }
        } catch (ParseException p) {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> createDevice(DeviceRequest deviceRequest) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        Optional<Device> deviceFormDB = deviceRepository.findByDeviceCode(deviceRequest.getDeviceCode());
        if (deviceFormDB.isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_DEVICE_CODE);
        }
        try {
            ModelMapper modelMapper = new ModelMapper();
            Device device = new Device();
            modelMapper.map(deviceRequest, device);
            device.setStatus(0);
            device.setDeviceId(0L);
            deviceRepository.save(device);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        } catch (Exception p) {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateDevice(UpdateDeviceRequest deviceRequest, Long deviceId) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;

        Optional<Device> deviceById = deviceRepository.findByDeviceId(deviceId);
        if (!deviceById.isPresent()) {
            throw new BaseException(ErrorCode.DEVICE_NOT_EXIST);
        }
        if (deviceById.get().getStatus() != 0) {
            throw new BaseException(ErrorCode.DEVICE_HAS_BEEN_BORROWED);
        }
        Device device = deviceById.get();
        Optional<Device> deviceByCode = deviceRepository.findByDeviceCode(deviceRequest.getDeviceCode());
        if ( !device.getDeviceCode().equalsIgnoreCase(deviceRequest.getDeviceCode())  && deviceByCode.isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_DEVICE_CODE);
        }
        try {

            ModelMapper modelMapper = new ModelMapper();
            if(deviceRequest.getDeviceName() == null){
                deviceRequest.setDeviceName(device.getDeviceName());
            }
            if(deviceRequest.getDeviceCode()== null){
                deviceRequest.setDeviceCode(device.getDeviceCode());
            }
            if (deviceRequest.getDeviceTypeId() == null){
                deviceRequest.setDeviceTypeId(device.getDeviceTypeId());
            }
            modelMapper.map(deviceRequest, device);
            device.setDeviceId(deviceId);
            device.setStatus(0);
            deviceRepository.save(device);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        } catch (Exception p) {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> deleteDevice(Long deviceId) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;

        Optional<Device> deviceById = deviceRepository.findByDeviceId(deviceId);
        if (!deviceById.isPresent()) {
            throw new BaseException(ErrorCode.DEVICE_NOT_EXIST);
        }
        if (deviceById.get().getStatus() == 1) {
            throw new BaseException(ErrorCode.DEVICE_HAS_BEEN_BORROWED);
        }
        try {
            deviceRepository.delete(deviceById.get());
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        } catch (Exception p) {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<DeviceResponse.DetailDeviceResponse, Void>> getDetailDevice(Long deviceId) {

        ResponseEntity<BaseResponse<DeviceResponse.DetailDeviceResponse, Void>> responseEntity = null;
        DeviceDto deviceDto = deviceRepository.getDetailDeviceById(deviceId);

        if (deviceDto == null) {
            throw new BaseException(ErrorCode.DEVICE_NOT_EXIST);
        }
        if (deviceDto.getStatus() == 1) {
            deviceDto.setIsAllowDelete(1);
        }
        DeviceResponse.DetailDeviceResponse detailDeviceResponse = new DeviceResponse.DetailDeviceResponse(deviceDto);
        responseEntity = BaseResponse.ofSucceeded(detailDeviceResponse);

        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<DeviceResponse, Pageable>> searchListDevice(String search, Integer isUser, Long deviceTypeId, Integer page, Integer limit) {

        ResponseEntity<BaseResponse<DeviceResponse, Pageable>> responseEntity = null;
        Pagination pagination = new Pagination(page - 1, limit);

        Page<DeviceDto> deviceDtoPage = deviceRepository.searchDeviceBy(search, isUser, deviceTypeId, pagination);
        List<DeviceDto> deviceDtos = deviceDtoPage.getContent();
        for (DeviceDto item : deviceDtos) {
            if(item.getStatus() == 1){
                item.setIsAllowDelete(1);
            }
        }
        pagination.setTotalRecords(deviceDtoPage);
        pagination.setPage(page);
        DeviceResponse deviceResponse = new DeviceResponse(deviceDtos);
        responseEntity = BaseResponse.ofSucceededOffset(deviceResponse, pagination);
        return responseEntity;
    }


    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> isRemainDeviceByDeviceTye(Long deviceTypeId) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        List<Device> deviceList = deviceRepository.findByDeviceTypeIdAndStatus(deviceTypeId, 0);
        if (deviceList.isEmpty()) {
            throw new BaseException(ErrorCode.DO_NOT_ENOUGH_DEVICE_TO_ASSIGN);
        }
        responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> returnDevice(Long borrowHistoryId, UserPrincipal userPrincipal) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        try {
            BorrowHistory borrowHistory = borrowHistoryRepository.findById(borrowHistoryId).orElse(null);
            if (borrowHistory != null) {
                Device device = deviceRepository.findById(borrowHistory.getDeviceId()).orElse(null);
                if (device != null) {
                    //update return date
                    Date currentDate = DateTimeUtil.getCurrentTime();
                    currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                    borrowHistory.setReturnDate(currentDate);
                    borrowHistoryRepository.save(borrowHistory);

                    //update status of device
                    device.setStatus(0);
                    deviceRepository.save(device);

                    //notification to all it-support
                    List<Person> allITSupport = personRepository.getMasterDataPersonByRole(CommonConstant.ROLE_ID_OF_IT_SUPPORT, null);
//                    Long currentUser = Long.valueOf(2);
                    Long currentUser = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
                    for (Person person : allITSupport) {
                        currentDate = DateTimeUtil.getCurrentTime();
                        currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
                        Notification notification = new Notification("retunred device " + device.getDeviceName() + " - " + device.getDeviceCode(),
                                0, "human-resource/borrow-device-history/detail/" + borrowHistoryId, 0, currentUser, person.getPersonId(), currentDate);
                        notificationRepository.save(notification);
                    }
                    responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
                }else {
                    responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
                }
            } else {
                responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
            }
        } catch (Exception e) {
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }
}
