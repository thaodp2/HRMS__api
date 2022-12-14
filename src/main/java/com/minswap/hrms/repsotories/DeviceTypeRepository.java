package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.RequestType;
import com.minswap.hrms.response.dto.DeviceTypeDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType,Long> {

    @Query("select dt.deviceTypeId from DeviceType dt")
    List<Long> getAllDeviceTypeId();

    List<DeviceType> findByDeviceTypeNameContainsIgnoreCase(String deviceTypeName);

    List<DeviceType> findByDeviceTypeNameContainsIgnoreCaseAndStatus(String deviceTypeName, Integer status);

    List<DeviceType> findByStatus(Integer status);
    List<DeviceType> findByDeviceTypeNameContainsIgnoreCase(String deviceTypeName, Pageable pageable);

    List<DeviceType> findByDeviceTypeNameIgnoreCase(String deviceTypeName);

}
