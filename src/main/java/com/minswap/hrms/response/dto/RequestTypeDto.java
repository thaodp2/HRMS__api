package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestTypeDto {
    @JsonProperty("requestTypeId")
    private int requestTypeId;

    @JsonProperty("requestTypeName")
    private String requestTypeName;
}
