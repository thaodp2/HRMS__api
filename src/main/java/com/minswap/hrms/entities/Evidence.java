package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = Evidence.TABLE_NAME)
public class Evidence {

    public static final String TABLE_NAME = "evidence";

    public Evidence(Long requestId, String image) {
        this.requestId = requestId;
        this.image = image;
    }

    @Id
    @Column(name = "evidence_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evidenceId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "image")
    private String image;
}
