package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.BorrowRequestDetailDto;
import com.minswap.hrms.response.dto.RequestDetailDto;
import lombok.*;

@Data
public class RequestDetailRespone {

    @JsonProperty(value = "requestDetail")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RequestDetailDto requestDetailDto;

    @JsonProperty("borrowRequestDetail")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BorrowRequestDetailDto borrowRequestDetailDto;

    public RequestDetailRespone(RequestDetailDto requestDetailDto) {
        this.requestDetailDto = requestDetailDto;
    }

    public RequestDetailRespone(BorrowRequestDetailDto borrowRequestDetailDto) {
        this.borrowRequestDetailDto = borrowRequestDetailDto;
    }
}
