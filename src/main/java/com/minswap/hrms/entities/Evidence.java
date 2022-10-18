package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = Evidence.TABLE_NAME)
public class Evidence {

    public static final String TABLE_NAME = "evidence";

    @Id
    @Column(name = "evidence_id")
    private int evidenceId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "image")
    private String image;
}
