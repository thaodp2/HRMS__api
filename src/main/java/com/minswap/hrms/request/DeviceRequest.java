package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DeviceRequest extends BasicRequest {
    @JsonProperty("deviceName")
    @NotNull(message = "421")
    @NotEmpty(message = "421")
    private String deviceName;
    @JsonProperty("deviceCode")
    @NotNull(message = "422")
    @NotEmpty(message = "422")
    private String deviceCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("deviceTypeId")
    @NotNull(message = "423")
    private Long deviceTypeId;

    @JsonCreator
    public DeviceRequest(String deviceName, String deviceCode, String description, Long deviceTypeId) {
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.description = description;
        this.deviceTypeId = deviceTypeId;
    }
}
