package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OfficeTimeDto {

    public OfficeTimeDto(String timeStart, String timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    private String timeStart;
    private String timeEnd;

}
