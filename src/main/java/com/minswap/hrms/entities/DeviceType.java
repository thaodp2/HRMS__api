package com.minswap.hrms.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = DeviceType.TABLE_NAME)
public class DeviceType {
    public static final String TABLE_NAME = "device_type";

    public DeviceType(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public DeviceType(String deviceTypeName, Integer status) {
        this.deviceTypeName = deviceTypeName;
        this.status = status;
    }

    @Id
    @Column(name = "device_type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long deviceTypeId;

    @Column(name = "device_type")
    private String deviceTypeName;

    @Column(name = "status")
    private Integer status;

}
