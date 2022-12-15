package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder("id")
public class DeviceTypeDto {

    @JsonProperty("id")
    private Long deviceTypeId;

    private String deviceTypeName;
    @JsonProperty("isDeleted")
    private Integer status;
    private Integer isAllowDelete;

}
