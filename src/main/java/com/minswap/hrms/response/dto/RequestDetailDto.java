package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RequestDetailDto {

    public RequestDetailDto(Long requestId, String sender, String requestTypeName, Date createDate, Date startTime,
                            Date endTime, String image, String reason, String status, String receiver, String deviceTypeName, Date approvalDate) {
        this.requestId = requestId;
        this.sender = sender;
        this.requestTypeName = requestTypeName;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.reason = reason;
        this.status = status;
        this.receiver = receiver;
        this.deviceTypeName = deviceTypeName;
        this.approvalDate = approvalDate;
    }

    private Long requestId;

    private String sender;

    private String requestTypeName;

    private Date createDate;

    private Date startTime;

    private Date endTime;

    private String image;

    private String reason;

    private String status;

    private String receiver;

    private String deviceTypeName;

    private Date approvalDate;


}
