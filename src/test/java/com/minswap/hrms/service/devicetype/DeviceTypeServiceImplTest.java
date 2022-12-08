package com.minswap.hrms.service.devicetype;

import com.minswap.hrms.entities.Device;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceRepository;
import com.minswap.hrms.repsotories.DeviceTypeRepository;
import com.minswap.hrms.response.DeviceTypeResponse;
import com.minswap.hrms.response.MasterDataResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeviceTypeServiceImplTest {
    @Mock
    DeviceTypeRepository deviceTypeRepository;
    @Mock
    DeviceRepository deviceRepository;
    @Mock
    Logger log;
    @InjectMocks
    DeviceTypeServiceImpl deviceTypeServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllDeviceType() throws Exception {
        when(deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(anyString())).thenReturn(List.of(new DeviceType("deviceTypeName")));
        when(deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(anyString(), any())).thenReturn(List.of(new DeviceType("deviceTypeName")));
        when(deviceRepository.findByDeviceTypeIdAndStatus(anyLong(), anyInt())).thenReturn(List.of(new Device(Long.valueOf(1), "deviceName", "deviceCode", "description", Integer.valueOf(0), Long.valueOf(1))));

        ResponseEntity<BaseResponse<DeviceTypeResponse.DeviceTypeDtoResponse, Pageable>> result = deviceTypeServiceImpl.getAllDeviceType(Integer.valueOf(1), Integer.valueOf(10), "deviceTypeName");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testCheckIsAllowDelete() throws Exception {
        when(deviceRepository.findByDeviceTypeIdAndStatus(anyLong(), anyInt())).thenReturn(List.of(new Device(Long.valueOf(1), "deviceName", "deviceCode", "description", Integer.valueOf(0), Long.valueOf(1))));
        boolean result = deviceTypeServiceImpl.checkIsAllowDelete(Long.valueOf(1));
        Assert.assertEquals(false, result);
    }

    @Test
    public void testCheckDuplicateDeviceType() throws Exception {
        when(deviceTypeRepository.findByDeviceTypeNameIgnoreCase(anyString())).thenReturn(List.of(new DeviceType("deviceTypeName")));

        boolean result = deviceTypeServiceImpl.checkDuplicateDeviceType(List.of("String"));
        Assert.assertEquals(true, result);
    }

    @Test
    public void testCheckItemEmpty() throws Exception {
        boolean result = deviceTypeServiceImpl.checkItemEmpty(List.of("String"));
        Assert.assertEquals(false, result);
    }

    @Test
    public void testEditDeviceType() throws Exception {
        when(deviceTypeRepository.findByDeviceTypeNameIgnoreCase(anyString())).thenReturn(List.of(new DeviceType("deviceTypeName")));
        assertThrows(BaseException.class, () -> deviceTypeServiceImpl.editDeviceType(Long.valueOf(1), "deviceTypeName"));
    }

    @Test
    public void testDeleteDeviceType() throws Exception {
        when(deviceRepository.findByDeviceTypeId(anyLong())).thenReturn(List.of(new Device(Long.valueOf(1), "deviceName", "deviceCode", "description", Integer.valueOf(0), Long.valueOf(1))));
        assertThrows(BaseException.class, () -> deviceTypeServiceImpl.deleteDeviceType(Long.valueOf(1)));
    }

    @Test
    public void testGetMasterDataDeviceType() throws Exception {
        when(deviceTypeRepository.findByDeviceTypeNameContainsIgnoreCase(anyString())).thenReturn(List.of(new DeviceType("deviceTypeName")));
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> result = deviceTypeServiceImpl.getMasterDataDeviceType("search");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }


    @Test
    @DisplayName("Should throw an exception when the devicetypename is null")
    public void createDeviceTypeWhenDeviceTypeNameIsNullThenThrowException() {
        List<String> deviceTypeName = null;
        assertThrows(BaseException.class, () -> deviceTypeServiceImpl.createDeviceType(deviceTypeName));
    }

    @Test
    @DisplayName("Should throw an exception when the devicetypename is empty")
    public void createDeviceTypeWhenDeviceTypeNameIsEmptyThenThrowException() {
        List<String> deviceTypeName = new ArrayList<>();
        assertThrows(BaseException.class, () -> deviceTypeServiceImpl.createDeviceType(deviceTypeName));
    }

    @Test
    @DisplayName("Should throw an exception when the item in list is empty")
    public void createDeviceTypeWhenItemInListIsEmptyThenThrowException() {
        List<String> deviceTypeName = new ArrayList<>();
        deviceTypeName.add("");
        assertThrows(BaseException.class, () -> deviceTypeServiceImpl.createDeviceType(deviceTypeName));
    }

    @Test
    @DisplayName(
            "Should return httpstatus ok when the device type name is not duplicate and not empty")
    public void createDeviceTypeWhenNotDuplicateAndNotEmptyThenReturnHttpStatusOK() {
        List<String> deviceTypeName = new ArrayList<>();
        deviceTypeName.add("Laptop");
        when(deviceTypeRepository.findByDeviceTypeNameIgnoreCase(anyString()))
                .thenReturn(new ArrayList<>());
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity =
                deviceTypeServiceImpl.createDeviceType(deviceTypeName);
        assertEquals(HttpStatus.OK, responseEntity.getBody().getData());
    }

    @Test
    @DisplayName("Should throw an exception when the device type name is duplicate")
    public void createDeviceTypeWhenDuplicateThenThrowException() {
        List<String> deviceTypeName = new ArrayList<>();
        deviceTypeName.add("Laptop");
        deviceTypeName.add("Laptop");
        assertThrows(BaseException.class, () -> deviceTypeServiceImpl.createDeviceType(deviceTypeName));
    }
}

