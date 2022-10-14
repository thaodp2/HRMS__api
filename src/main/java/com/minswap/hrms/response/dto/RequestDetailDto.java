package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestDetailDto {
    public RequestDetailDto(String sender, String requestTypeName, Date startTime, Date endTime, String image,
                            String reason, String status, String receiver, Date approvalDate) {
        this.sender = sender;
        this.requestTypeName = requestTypeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.reason = reason;
        this.status = status;
        this.receiver = receiver;
        this.approvalDate = approvalDate;
    }
    private String sender;

    private String requestTypeName;

    private Date startTime;

    private Date endTime;

    private String image;

    private String reason;

    private String status;

    private String receiver;

    private Date approvalDate;


}
