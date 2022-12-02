package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTypeDto {

    @JsonProperty("id")
    private Long deviceTypeId;

    private String deviceTypeName;

    private Integer isAllowDelete;

}
