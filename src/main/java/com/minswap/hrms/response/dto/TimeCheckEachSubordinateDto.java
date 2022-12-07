package com.minswap.hrms.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TimeCheckEachSubordinateDto {
    private Long id;

    private String rollNumber;

    private String fullName;

    private DailyTimeCheckDto mon;

    private DailyTimeCheckDto tue;

    private DailyTimeCheckDto wed;

    private DailyTimeCheckDto thu;

    private DailyTimeCheckDto fri;

    private DailyTimeCheckDto sat;

    private DailyTimeCheckDto sun;

    public TimeCheckEachSubordinateDto() {
    }
}
