package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceTypeIdAndStatusAndDeviceNameContainsIgnoreCase(Long deviceTypeId, Integer status, String deviceName);

    List<Device> findByDeviceTypeIdAndStatus(Long deviceTypeId, Integer status);

    Optional<Device> findByDeviceCode(String deviceCode);

    Optional<Device> findByDeviceId(Long deviceId);
}
