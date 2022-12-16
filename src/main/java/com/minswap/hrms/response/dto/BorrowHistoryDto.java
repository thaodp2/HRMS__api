package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.minswap.hrms.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder("id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BorrowHistoryDto {
    public BorrowHistoryDto(Long borrowHistoryId, String rollNumber, String fullName, String deviceTypeName, String deviceName, String deviceCode, Date borrowDate, Date returnDate, String status) {
        this.borrowHistoryId = borrowHistoryId;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.deviceTypeName = deviceTypeName;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
//        this.isReturned = isReturned;
    }

    @JsonProperty("id")
    private Long borrowHistoryId;

    private String rollNumber;

    private String fullName;

    private String deviceTypeName;

    private String deviceName;

    private String deviceCode;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date borrowDate;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date returnDate;

//    private Integer isReturned;
    private String status;

}
