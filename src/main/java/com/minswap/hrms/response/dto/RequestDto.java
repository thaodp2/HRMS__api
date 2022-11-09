package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.repsotories.RequestTypeRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder("id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class RequestDto {
    public RequestDto(Long requestId, String personName, String requestTypeName, Date createDate, Date startTime,
                      Date endTime, String reason, String status, String receiver, String deviceTypeName, Date approvalDate) {
        this.requestId = requestId;
        this.personName = personName;
        this.requestTypeName = requestTypeName;
        this.createDate = createDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.receiver = receiver;
        this.deviceTypeName = deviceTypeName;
        this.approvalDate = approvalDate;
    }

    @JsonProperty("id")
    private Long requestId;

    private String rollNumber;
    private String personName;
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
    private String deviceTypeName;
    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date approvalDate;

    private double timeRemaining;

    private double budget;

}