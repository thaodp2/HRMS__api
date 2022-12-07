package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceRepository;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceTypeServiceImplTest {

    @Mock
    DeviceTypeRepository deviceTypeRepository;

    @Mock
    DeviceRepository deviceRepository;

    @InjectMocks
    DeviceTypeServiceImpl deviceTypeService;

    @Test
    @DisplayName("Should throw an exception when the devicetypename is null")
    void createDeviceTypeWhenDeviceTypeNameIsNullThenThrowException() {
        List<String> deviceTypeName = null;
        assertThrows(BaseException.class, () -> deviceTypeService.createDeviceType(deviceTypeName));
    }

    @Test
    @DisplayName("Should throw an exception when the devicetypename is empty")
    void createDeviceTypeWhenDeviceTypeNameIsEmptyThenThrowException() {
        List<String> deviceTypeName = new ArrayList<>();
        assertThrows(BaseException.class, () -> deviceTypeService.createDeviceType(deviceTypeName));
    }

    @Test
    @DisplayName("Should throw an exception when the item in list is empty")
    void createDeviceTypeWhenItemInListIsEmptyThenThrowException() {
        List<String> deviceTypeName = new ArrayList<>();
        deviceTypeName.add("");
        assertThrows(BaseException.class, () -> deviceTypeService.createDeviceType(deviceTypeName));
    }

    @Test
    @DisplayName(
            "Should return httpstatus ok when the device type name is not duplicate and not empty")
    void createDeviceTypeWhenNotDuplicateAndNotEmptyThenReturnHttpStatusOK() {
        List<String> deviceTypeName = new ArrayList<>();
        deviceTypeName.add("Laptop");
        when(deviceTypeRepository.findByDeviceTypeNameIgnoreCase(anyString()))
                .thenReturn(new ArrayList<>());
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity =
                deviceTypeService.createDeviceType(deviceTypeName);
        assertEquals(HttpStatus.OK, responseEntity.getBody().getData());
    }

    @Test
    @DisplayName("Should throw an exception when the device type name is duplicate")
    void createDeviceTypeWhenDuplicateThenThrowException() {
        List<String> deviceTypeName = new ArrayList<>();
        deviceTypeName.add("Laptop");
        deviceTypeName.add("Laptop");
        assertThrows(BaseException.class, () -> deviceTypeService.createDeviceType(deviceTypeName));
    }
}