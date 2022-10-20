package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTypeRepository extends JpaRepository<DeviceType,Integer> {

    @Query("select dt.deviceTypeId from DeviceType dt")
    List<Long> getAllDeviceTypeId();

}
