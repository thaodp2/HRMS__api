package com.minswap.hrms.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = RequestType.TABLE_NAME)
public class RequestType {
    public static final String TABLE_NAME = "request_type";
    @Id
    @Column(name = "request_type_id")
    private int requestTypeId;

    @Column(name = "request_type_name")
    private String requestTypeName;
}
