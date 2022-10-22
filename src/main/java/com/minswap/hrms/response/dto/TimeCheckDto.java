package com.minswap.hrms.response.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TimeCheckDto {

    private Long personId;

    private Double iloe;

    private Date timeIn;

    private Date timeOut;

    private Double ot;

    private Double workingTime;

    public TimeCheckDto(Long personId, Double iloe, Date timeIn, Date timeOut, Double ot, Double workingTime) {
        this.personId = personId;
        this.iloe = iloe;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.ot = ot;
        this.workingTime = workingTime;
    }
}
