package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.PersonDto;
import com.minswap.hrms.response.dto.RequestDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {
    private Integer requestId;

    private String requestType;

    private String personName;

    private String startTime;

    private String endTime;

    private String reason;

    private String createDate;

    private String status;

    public static RequestResponse of(RequestDto requestDto) {
        return RequestResponse.builder()
                .requestId(requestDto.getRequestId())
                .requestType(requestDto.getRequestType())
                .personName(requestDto.getPersonName())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .reason(requestDto.getReason())
                .createDate(requestDto.getCreateDate())
                .status(requestDto.getStatus())
                .build();
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestListResponse {
        @JsonProperty("requestList")
        private List<RequestResponse> requestList;

        public static RequestListResponse of(List<RequestResponse> requestResponses) {
            return builder().requestList(requestResponses).build();
        }
    }
}


