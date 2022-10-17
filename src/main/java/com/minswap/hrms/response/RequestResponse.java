package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.*;

@Data
public class RequestResponse {

    @JsonProperty(value = "items")
    private RequestDto requestDto;

    public RequestResponse(RequestDto requestDto) {
        this.requestDto = requestDto;
    }

}
