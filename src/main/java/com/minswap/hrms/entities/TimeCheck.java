package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = TimeCheck.TABLE_NAME)
public class TimeCheck {
    public static final String TABLE_NAME = "time_check";

    @Id
    @Column(name = "time_check_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeCheckId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "in_late")
    private Double inLate;

    @Column(name = "out_early")
    private Double outEarly;

    @Column(name = "time_in")
    private Date timeIn;

    @Column(name = "time_out")
    private Date timeOut;

    @Column(name = "ot")
    private Double ot;

    @Column(name = "working_time")
    private Double workingTime;

}
