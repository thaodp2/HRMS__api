package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.*;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestResponse {

    @JsonProperty(value = "item")
    private RequestDto requestDto;

    public RequestResponse(RequestDto requestDto) {
        this.requestDto = requestDto;
    }



    @Data
    public static class RequestListResponse{
        @JsonProperty(value = "items")
        private List<RequestDto> requestDtos;
        public RequestListResponse(List<RequestDto> requestDtos) {
            this.requestDtos = requestDtos;
        }
    }

}
