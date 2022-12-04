package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.RequestType;
import com.minswap.hrms.response.dto.DeviceTypeDto;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.Data;

import java.util.List;
@Data
public class DeviceTypeResponse {
    @JsonProperty(value = "items")
    private List<DeviceType> deviceTypes;
    public DeviceTypeResponse(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    @Data
    public static class DeviceTypeDtoResponse{
        @JsonProperty(value = "items")
        private List<DeviceTypeDto> deviceTypeDtos;
        public DeviceTypeDtoResponse(List<DeviceTypeDto> deviceTypeDtos) {
            this.deviceTypeDtos = deviceTypeDtos;
        }
    }
}
