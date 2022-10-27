package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.AnnualLeaveBudget;
import com.minswap.hrms.entities.OTBudget;
import com.minswap.hrms.response.dto.AnnualLeaveBudgetDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OTBudgetRepository extends JpaRepository<OTBudget, Long> {
//    @Query("SELECT new com.minswap.hrms.response.dto.OTBudgetDto(alb.annualLeaveBudget, alb.numberOfDayOff, alb.remainDayOff) " +
//            "from OTBudget ob " +
//            "left join Request r on r.personId = alb.personId " +
//            "where r.requestId =:id and alb.year =:year")
//    AnnualLeaveBudgetDto getAnnualLeaveBudgetByPersonId(@Param("id") Long id,
//                                                        @Param("year") int year);
}
