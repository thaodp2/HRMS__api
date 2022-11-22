package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeviceTypeRequest extends BasicRequest {
    private List<String> deviceTypeNames;

    private String deviceTypeName;

    @JsonCreator
    public DeviceTypeRequest(List<String> deviceTypeNames, String deviceTypeName) {
        this.deviceTypeNames = deviceTypeNames;
        this.deviceTypeName = deviceTypeName;
    }
}
