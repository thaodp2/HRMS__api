package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.AnnualLeaveBudget;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.AnnualLeaveBudgetDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Year;

@Repository
public interface AnnualLeaveBudgetRepository extends JpaRepository<AnnualLeaveBudget, Long> {

    @Query("SELECT new com.minswap.hrms.response.dto.AnnualLeaveBudgetDto(alb.annualLeaveBudget, alb.numberOfDayOff, alb.remainDayOff) " +
            "from AnnualLeaveBudget alb " +
            "left join Request r on r.personId = alb.personId " +
            "where r.requestId =:id and alb.year =:year")
    AnnualLeaveBudgetDto getAnnualLeaveBudgetByRequestId(@Param("id") Long id,
                                                         @Param("year") int year);

    @Modifying
    @Transactional
    @Query("Update AnnualLeaveBudget alb set alb.numberOfDayOff =:numberOfDayOff, alb.remainDayOff =:remainDayOff " +
            "where alb.personId =:id and alb.year=:year")
    Integer updateAnnualLeaveBudget(@Param("id") Long id,
                                    @Param("numberOfDayOff") double numberOfDayOff,
                                    @Param("remainDayOff") double remainDayOff,
                                    @Param("year") int year);
}
