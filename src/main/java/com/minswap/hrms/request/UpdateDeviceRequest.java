package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UpdateDeviceRequest  extends BasicRequest{

    @JsonProperty("deviceName")
    private String deviceName;

    @JsonProperty("deviceCode")
    private String deviceCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("deviceTypeId")
    private Long deviceTypeId;

}
