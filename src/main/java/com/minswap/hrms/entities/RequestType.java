package com.minswap.hrms.entities;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = RequestType.TABLE_NAME)
public class RequestType {

    public static final String TABLE_NAME = "request_type";

    @Id
    @Column(name = "request_type_id")
    private Long requestTypeId;

    @Column(name = "request_type_name")
    private String requestTypeName;
}
