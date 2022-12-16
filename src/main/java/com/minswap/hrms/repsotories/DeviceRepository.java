package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Device;
import com.minswap.hrms.response.dto.DeviceDetailDto;
import com.minswap.hrms.response.dto.DeviceDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceTypeIdAndStatusAndDeviceNameContainsIgnoreCase(Long deviceTypeId, Integer status, String deviceName);

    List<Device> findByDeviceTypeIdAndStatus(Long deviceTypeId, Integer status);

    List<Device> findByDeviceTypeId(Long deviceTypeId);

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
            " WHERE d.status <> 2 " +
            " and ( :deviceTypeId is null or d.deviceTypeId = :deviceTypeId )" +
            " and (:status is null or d.status = :status)" +
            " and (:search is null or d.deviceName like %:search% or d.deviceCode like %:search%  )" +
            " order by d.deviceId desc ")
    Page<DeviceDto> searchDeviceBy(String search, Integer status, Long deviceTypeId, Pageable pageable);

    @Query("SELECT new com.minswap.hrms.response.dto.DeviceDetailDto(" +
            " d.deviceId," +
            " d.deviceName," +
            " d.deviceCode," +
            " d.description," +
            " d.status," +
            " d.deviceTypeId, 0) " +
            " from Device d " +
            " WHERE d.deviceId = :deviceId ")
    DeviceDetailDto getDetailDeviceById(Long deviceId);

    @Modifying
    @Transactional
    @Query("update Device d set d.status = :deviceStatus where d.deviceId = :deviceId")
    Integer updateDeviceStatus(@Param("deviceId") Long deviceId,
                               @Param("deviceStatus") Integer deviceStatus);
}
