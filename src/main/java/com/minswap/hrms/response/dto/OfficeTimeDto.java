package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OfficeTimeDto {

    public OfficeTimeDto(String timeStart,
                         String timeEnd,
                         String lunchBreakStartTime,
                         String lunchBreakEndTime) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.lunchBreakStartTime = lunchBreakStartTime;
        this.lunchBreakEndTime = lunchBreakEndTime;
    }
    private String timeStart;
    private String timeEnd;
    private String lunchBreakStartTime;
    private String lunchBreakEndTime;

}
