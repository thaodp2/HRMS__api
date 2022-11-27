package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceDto {

    @JsonProperty("id")
    private Long deviceId;

    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("deviceTypeName")
    private String deviceTypeName;
}
