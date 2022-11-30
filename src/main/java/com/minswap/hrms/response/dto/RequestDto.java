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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder("id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDto {
    public RequestDto(Long requestId, String rollNumber, String personName, Long requestTypeId, String requestTypeName, Date createDate, Date startTime,
                      Date endTime, String reason, String status, String receiver, Long deviceTypeId, String deviceTypeName, Date approvalDate,
                      Integer isAssigned, Date maximumTimeToRollback) {
        this.requestId = requestId;
        this.rollNumber = rollNumber;
        this.personName = personName;
        this.requestTypeId = requestTypeId;
        this.requestTypeName = requestTypeName;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.receiver = receiver;
        this.deviceTypeId = deviceTypeId;
        this.deviceTypeName = deviceTypeName;
        this.approvalDate = approvalDate;
        this.isAssigned = isAssigned;
        this.maximumTimeToRollback = maximumTimeToRollback;
    }

    public RequestDto(Long requestId, String rollNumber, String personName, Date createDate, String reason, String receiver, String deviceTypeName, Date approvalDate, Integer isAssigned) {
        this.requestId = requestId;
        this.rollNumber = rollNumber;
        this.personName = personName;
        this.createDate = createDate;
        this.reason = reason;
        this.receiver = receiver;
        this.deviceTypeName = deviceTypeName;
        this.approvalDate = approvalDate;
        this.isAssigned = isAssigned;
    }

    @JsonProperty("id")
    private Long requestId;

    private String rollNumber;
    private String personName;

    private Long requestTypeId;
    private String requestTypeName;
    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date createDate;
    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date startTime;
    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date endTime;
    private List<String> listEvidence;
    private String reason;
    private String status;
    private String receiver;
    private Long deviceTypeId;

    private String deviceTypeName;
    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date approvalDate;

    private double timeRemaining;

    private int isAllowRollback;

    private Integer isAssigned;

    private Long deviceId;

    private Date maximumTimeToRollback;
}