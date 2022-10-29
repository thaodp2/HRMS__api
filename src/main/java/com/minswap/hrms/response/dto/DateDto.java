package com.minswap.hrms.response.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DateDto {

    public DateDto(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date startTime;
    private Date endTime;
}
