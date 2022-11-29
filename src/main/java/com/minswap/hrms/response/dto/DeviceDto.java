package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;

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

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("deviceTypeName")
    private String deviceTypeName;
}
