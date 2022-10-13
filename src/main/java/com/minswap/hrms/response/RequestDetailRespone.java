package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.RequestDetailDto;
import lombok.*;

@Data
public class RequestDetailRespone {

    @JsonProperty("requestDetail")
    private RequestDetailDto requestDetailDto;

    public RequestDetailRespone(RequestDetailDto requestDetailDto) {
        this.requestDetailDto = requestDetailDto;
    }
}
