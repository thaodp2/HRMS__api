package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.response.dto.EmployeeTimeRemainingDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Year;

public interface EmployeeTimeRemainingRepository extends JpaRepository<LeaveBudget, Long> {

    @Query("select new com.minswap.hrms.response.dto.EmployeeTimeRemainingDto(lb.remainDayOff) " +
            "from LeaveBudget lb " +
            "where lb.personId=:personId and lb.requestTypeId=:requestTypeId and lb.year=:year")
    EmployeeTimeRemainingDto getLeaveBudgetTimeRemaining(@Param("personId") Long personId,
                                                         @Param("requestTypeId") Long requestTypeId,
                                                         @Param("year") Year year);
   }
