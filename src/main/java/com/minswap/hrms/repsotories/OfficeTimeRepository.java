package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.response.dto.OfficeTimeDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface OfficeTimeRepository extends JpaRepository<OfficeTime, Long> {

    @Query(value = "SELECT ot.office_time_id  FROM office_time ot ORDER BY ot.office_time_id  DESC LIMIT 1  ", nativeQuery = true)
    Long getPresentOfficeTimeId();

    Optional<OfficeTime> findOfficeTimeByOfficeTimeId(@Param("officeTimeId") Long officeTimeId);

    @Query("select new com.minswap.hrms.response.dto.OfficeTimeDto(ot.timeStart, ot.timeFinish, " +
            "ot.lunchBreakStartTime, ot.lunchBreakEndTime) from OfficeTime ot")
    OfficeTimeDto getOfficeTime();
}
