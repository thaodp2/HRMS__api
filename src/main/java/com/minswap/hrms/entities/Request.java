package com.minswap.hrms.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Request.TABLE_NAME)
public class Request {
    public static final String TABLE_NAME = "request";

    public Request(Long requestTypeId, Long personId, Long deviceTypeId, Date startTime,
                   Date endTime, String reason, Date createDate, String status) {
        this.requestTypeId = requestTypeId;
        this.personId = personId;
        this.deviceTypeId = deviceTypeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.createDate = createDate;
        this.status = status;
    }

    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(name = "request_type_id")
    private Long requestTypeId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "device_type_id")
    private Long deviceTypeId;

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

    @Column(name = "is_assigned")
    private Integer isAssigned;

    @Column(name = "maximum_time_to_rollback")
    private Date maximumTimeToRollback;
}
