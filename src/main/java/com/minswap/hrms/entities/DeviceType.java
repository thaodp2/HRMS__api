package com.minswap.hrms.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = DeviceType.TABLE_NAME)
public class DeviceType {
    public static final String TABLE_NAME = "device_type";

    public DeviceType(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    @Id
    @Column(name = "device_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long deviceTypeId;

    @Column(name = "device_type")
    private String deviceTypeName;

}
