package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minswap.hrms.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DailyTimeCheckDto {

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date timeIn;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date timeOut;

    private Double inLate;

    private Double outEarly;
}
