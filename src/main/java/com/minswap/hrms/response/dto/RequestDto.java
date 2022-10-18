package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.repsotories.RequestTypeRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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

    private Long requestId;
    private String personName;
    private String requestTypeName;
    private Date createDate;
    private Date startTime;
    private Date endTime;
    private List<String> image;
    private String reason;
    private String status;
    private String receiver;
    private String deviceTypeName;
    private Date approvalDate;

}
// t xóa 1 trường image đi r, sửa lại câu query đi