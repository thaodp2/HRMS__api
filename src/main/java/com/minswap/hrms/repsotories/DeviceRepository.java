package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Device;
import com.minswap.hrms.response.dto.DeviceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceTypeIdAndStatusAndDeviceNameContainsIgnoreCase(Long deviceTypeId, Integer status, String deviceName);

    List<Device> findByDeviceTypeIdAndStatus(Long deviceTypeId, Integer status);

    Optional<Device> findByDeviceCode(String deviceCode);

    Optional<Device> findByDeviceId(Long deviceId);

    @Query("SELECT new com.minswap.hrms.response.dto.DeviceDto(" +
            " d.deviceId," +
            " d.deviceName," +
            " d.deviceCode," +
            " d.description," +
            " d.status," +
            " dt.deviceTypeName, 0) " +
            " from Device d " +
            " join DeviceType dt on d.deviceTypeId = dt.deviceTypeId " +
            " WHERE 1 = 1 " +
            " and ( :deviceTypeId is null or d.deviceTypeId = :deviceTypeId )" +
            " and (:status is null or d.status = :status)" +
            " and (:search is null or d.deviceName like %:search% or d.deviceCode like %:search%  )")
    Page<DeviceDto> searchDeviceBy(String search, Integer status, Long deviceTypeId, Pageable pageable);

    @Query("SELECT new com.minswap.hrms.response.dto.DeviceDto(" +
            " d.deviceId," +
            " d.deviceName," +
            " d.deviceCode," +
            " d.description," +
            " d.status," +
            " dt.deviceTypeName, 0) " +
            " from Device d " +
            " join DeviceType dt on d.deviceTypeId = dt.deviceTypeId " +
            " WHERE d.deviceId = :deviceId ")
    DeviceDto getDetailDeviceById(Long deviceId);
}
