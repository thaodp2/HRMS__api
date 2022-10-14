package com.minswap.hrms.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Request.TABLE_NAME)
public class Request {
    public static final String TABLE_NAME = "request";
    @Id
    @Column(name = "request_id")
    private int requestId;

    @Column(name = "request_type_id")
    private int requestTypeId;

    @Column(name = "person_id")
    private int personId;

    @Column(name = "device_type_id")
    private int deviceTypeId;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "reason")
    private String reason;

    @Column(name = "create_date")
    private String createDate;

    @Column(name = "approval_date")
    private String approvalDate;

    @Column(name = "status")
    private String status;
}
