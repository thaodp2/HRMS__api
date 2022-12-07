package com.minswap.hrms.service.device;

import com.minswap.hrms.entities.Device;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DeviceRepository;
import com.minswap.hrms.request.DeviceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    @DisplayName("Should throw an exception when the device code is already taken")
    void createDeviceWhenDeviceCodeIsAlreadyTakenThenThrowException() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setDeviceCode("123");
        deviceRequest.setDeviceName("abc");
        deviceRequest.setDeviceTypeId(1L);
        Optional<Device> device = Optional.of(new Device());
        when(deviceRepository.findByDeviceCode(anyString())).thenReturn(device);
        assertThrows(BaseException.class, () -> deviceService.createDevice(deviceRequest));
    }

    @Test
    @DisplayName("Should return httpstatus.ok when the device code is not taken")
    void createDeviceWhenDeviceCodeIsNotTakenThenReturnHttpStatusOk() {
        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setDeviceCode("123");
        deviceRequest.setDeviceName("abc");
        deviceRequest.setDeviceTypeId(1L);
        when(deviceRepository.findByDeviceCode(anyString())).thenReturn(Optional.empty());
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity =
                deviceService.createDevice(deviceRequest);
        assertEquals(HttpStatus.OK, responseEntity.getBody().getData());
    }
}