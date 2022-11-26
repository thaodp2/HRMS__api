package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceTypeIdAndStatusAndDeviceNameContainsIgnoreCase(Long deviceTypeId, Integer status, String deviceName);

    List<Device> findByDeviceTypeIdAndStatus(Long deviceTypeId, Integer status);

}
