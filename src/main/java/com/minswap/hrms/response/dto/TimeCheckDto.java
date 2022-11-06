package com.minswap.hrms.response.dto;

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

    private Date date;

    private Date timeIn;

    private Date timeOut;

    private Double inLate;

    private Double outEarly;

    private Double ot;

    private Double workingTime;

    private String requestTypeName;

}
