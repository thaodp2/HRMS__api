package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.DeviceDetailDto;
import com.minswap.hrms.response.dto.DeviceDto;
import lombok.Data;

import java.util.List;

public class DeviceResponse {

    @JsonProperty(value = "items")
    private List<DeviceDto> devices;

    public DeviceResponse(List<DeviceDto> devices) {
        this.devices = devices;
    }

    @Data
    public static class DetailDeviceResponse{

      private DeviceDetailDto deviceDetailDto;

        public DetailDeviceResponse(DeviceDetailDto deviceDetailDto) {
            this.deviceDetailDto = deviceDetailDto;
        }
    }
}
