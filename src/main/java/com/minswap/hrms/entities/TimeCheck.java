package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = TimeCheck.TABLE_NAME)
public class TimeCheck {
    public static final String TABLE_NAME = "time_check";

    @Id
    @Column(name = "time_check_id")
    private Long timeCheckId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "iloe")
    private Double iloe;

    @Column(name = "time_in")
    private Date timeIn;

    @Column(name = "time_out")
    private Date timeOut;

    @Column(name = "ot")
    private Double ot;

    @Column(name = "working_time")
    private Double workingTime;

}
