package com.minswap.hrms.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = Device.TABLE_NAME)
public class Device {
    public static final String TABLE_NAME = "device";

    @Id
    @Column(name = "device_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_code")
    private String deviceCode;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private Integer status;

    @Column(name = "device_type_id")
    private Long deviceTypeId;
}
