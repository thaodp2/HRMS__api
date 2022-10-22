package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class DeviceTypeRequest extends BasicRequest{
    @NotNull(message = "416")
    private String deviceTypeName;

    @JsonCreator
    public DeviceTypeRequest(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }
}
