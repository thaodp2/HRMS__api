package com.minswap.hrms.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = DeviceType.TABLE_NAME)
public class DeviceType {
    public static final String TABLE_NAME = "device_type";

    @Id
    @Column(name = "device_type_id")
    private Long deviceTypeId;

    @Column(name = "device_type")
    private String deviceTypeName;

}
