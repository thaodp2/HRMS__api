package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.RequestDetailDto;
import lombok.*;

@Data
public class RequestDetailRespone {

    @JsonProperty(value = "requestDetail")
    private RequestDetailDto requestDetailDto;

    public RequestDetailRespone(RequestDetailDto requestDetailDto) {
        this.requestDetailDto = requestDetailDto;
    }

}
