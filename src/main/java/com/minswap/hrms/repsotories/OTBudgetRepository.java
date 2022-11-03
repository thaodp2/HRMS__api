package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.OTBudget;
import com.minswap.hrms.response.dto.OTBudgetDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Year;

@Repository
public interface OTBudgetRepository extends JpaRepository<OTBudget, Long> {
    @Query("SELECT new com.minswap.hrms.response.dto.OTBudgetDto(ob.otHoursBudget, ob.hoursWorked) " +
            "from OTBudget ob " +
            "where ob.personId =:id and ob.year =:year and ob.month =:month")
    OTBudgetDto getOTBudgetByPersonId(@Param("id") Long id,
                                      @Param("year") Year year,
                                      @Param("month") int month);
    @Modifying
    @Transactional
    @Query("Update OTBudget ob set ob.hoursWorked =:hoursWorked " +
            "where ob.personId =:personId and ob.year=:year and ob.month =:month")
    Integer updateOTBudget(@Param("personId") Long personId,
                           @Param("year") Year year,
                           @Param("month") int month,
                           @Param("hoursWorked") double hoursWorked);
}
