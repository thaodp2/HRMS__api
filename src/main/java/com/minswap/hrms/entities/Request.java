package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = Request.TABLE_NAME)
public class Request {
    public static final String TABLE_NAME = "request";

    @Id
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "request_type_id")
    private int requestTypeId;

    @Column(name = "person_id")
    private int personId;

    @Column(name = "device_type_id")
    private int deviceTypeId;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "reason")
    private String reason;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "approval_date")
    private Date approvalDate;

    @Column(name = "status")
    private String status;
}
