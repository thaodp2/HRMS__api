package com.minswap.hrms.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "lunch_break_start_time")
    private String lunchBreakStartTime;

    @Column(name = "lunch_break_end_time")
    private String lunchBreakEndTime;


}
