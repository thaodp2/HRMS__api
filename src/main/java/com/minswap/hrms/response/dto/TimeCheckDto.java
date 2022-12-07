package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minswap.hrms.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class TimeCheckDto {

    private Long id;

    private Long personId;

    private String personName;

    private String rollNumber;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD, shape = JsonFormat.Shape.STRING)
    private Date date;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date timeIn;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date timeOut;

    private Double inLate;

    private Double outEarly;

    private Double ot;

    private Double workingTime;

    private String requestTypeName;

}
