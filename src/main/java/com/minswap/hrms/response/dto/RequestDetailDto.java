package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetailDto {

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("requestTypeName")
    private String requestTypeName;

    @JsonProperty("startTime")
    private Date startTime;

    @JsonProperty("endTime")
    private Date endTime;

    @JsonProperty("image")
    private String image;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("status")
    private String status;

    @JsonProperty("receiver")
    private String receiver;

    @JsonProperty("approvalDate")
    private Date approvalDate;

}
