package com.minswap.hrms.response.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DailyTimeCheckDto {

    private Long personId;

    private String personName;

    private Date date;

    private String timeIn;

    private String timeOut;
}
