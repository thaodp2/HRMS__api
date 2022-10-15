package com.minswap.hrms.response.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BorrowRequestDetailDto {

    public BorrowRequestDetailDto(Long requestId, String deviceTypeName, Date createDate, Date startTime, String reason,
                                  String status, String receiver, Date approvalDate) {
        this.requestId = requestId;
        this.deviceTypeName = deviceTypeName;
        this.createDate = createDate;
        this.startTime = startTime;
        this.reason = reason;
        this.status = status;
        this.receiver = receiver;
        this.approvalDate = approvalDate;
    }

    private Long requestId;

    private String deviceTypeName;

    private Date createDate;

    private Date startTime;

    private String reason;

    private String status;

    private String receiver;

    private Date approvalDate;
}
