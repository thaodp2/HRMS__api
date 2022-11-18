package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = OfficeTime.TABLE_NAME)
public class OfficeTime {

    public static final String TABLE_NAME = "office_time";

    @Id
    @Column(name = "office_time_id")
    private Long officeTimeId;

    @Column(name = "time_start")
    private String timeStart;

    @Column(name = "time_finish")
    private String timeFinish;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "person_id")
    private Long personId;


}
