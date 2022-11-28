package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Device;
import com.minswap.hrms.response.dto.DeviceDto;

import java.util.List;

public class DeviceResponse {

    @JsonProperty(value = "items")
    private List<DeviceDto> devices;

    public DeviceResponse(List<DeviceDto> devices) {
        this.devices = devices;
    }
}
