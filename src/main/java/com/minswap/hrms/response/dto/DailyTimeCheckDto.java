package com.minswap.hrms.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DailyTimeCheckDto {

    private Date timeIn;

    private Date timeOut;

    private Double inLate;

    private Double outEarly;
}
