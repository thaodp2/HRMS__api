package com.minswap.hrms.entities;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = MonetaryFine.TABLE_NAME)
public class MonetaryFine {

    public static final String TABLE_NAME = "monetary_fine";

    @Id
    @Column(name = "monetary_fine_id")
    private Long monetaryFine;

    @Column(name = "time_check_id")
    private Long timeCheckId;

    @Column(name = "fine_amount")
    private Double fineAmount;


}
